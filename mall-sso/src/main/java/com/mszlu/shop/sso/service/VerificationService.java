package com.mszlu.shop.sso.service;

import com.alibaba.fastjson.JSON;
import com.mszlu.shop.common.cache.CachePrefix;
import com.mszlu.shop.common.security.AuthUser;
import com.mszlu.shop.common.security.UserEnums;
import com.mszlu.shop.common.utils.token.SecretKeyUtil;
import com.mszlu.shop.common.utils.token.SecurityKey;
import com.mszlu.shop.model.buyer.eums.VerificationEnums;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
//滑块验证后，存了一个验证结果，检查有无过期


@Service
@Slf4j
public class VerificationService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    public boolean check(String uuid, VerificationEnums verificationEnums) {
        Boolean hasKey = redisTemplate.hasKey("VERIFICATION_IMAGE_RESULT_" + verificationEnums.name() + uuid);
        return hasKey != null && hasKey;
    }

    public AuthUser checkToken(String token) {
        try {
            Claims claims
                    = Jwts.parser()
                    .setSigningKey(SecretKeyUtil.generalKey())
                    .parseClaimsJws(token).getBody();
            //获取存储在claims中的用户信息
            String json = claims.get(SecurityKey.USER_CONTEXT).toString();
            AuthUser authUser = JSON.parseObject(json, AuthUser.class);

            //校验redis中是否有权限
            Boolean hasKey = redisTemplate.hasKey(CachePrefix.ACCESS_TOKEN.name() + UserEnums.MEMBER.name() + token);
            if (hasKey != null && hasKey) {
                return authUser;
            }
        }catch (ExpiredJwtException e) {
            //token过期了
            log.debug("user analysis exception:", e);
//            System.out.println("user analysis exception:"+ e);
        }
        return null;
    }
}
