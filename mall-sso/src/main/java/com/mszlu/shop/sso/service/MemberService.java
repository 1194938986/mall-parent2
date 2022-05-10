package com.mszlu.shop.sso.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mszlu.shop.common.cache.CachePrefix;
import com.mszlu.shop.common.context.ThreadContextHolder;
import com.mszlu.shop.common.security.AuthUser;
import com.mszlu.shop.common.security.Token;
import com.mszlu.shop.common.security.UserEnums;
import com.mszlu.shop.common.utils.token.TokenUtils;
import com.mszlu.shop.common.vo.Result;
import com.mszlu.shop.model.buyer.eums.ClientType;
import com.mszlu.shop.model.buyer.pojo.Member;
import com.mszlu.shop.model.buyer.vo.member.MemberVo;
import com.mszlu.shop.sso.mapper.MemberMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
public class MemberService {

    @Resource
    private MemberMapper memberMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    public Result<Token> usernameLogin(String username, String password) {
        LambdaQueryWrapper<Member> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Member::getUsername,username).eq(Member::getDisabled,false);
        Member member = memberMapper.selectOne(queryWrapper);
        if (member == null){
            return Result.fail(-999,"用户不存在");
        }

        //利用security的密码类
        if (!new BCryptPasswordEncoder().matches(password,member.getPassword())){
            return Result.fail(-999,"用户名或密码错误");
        }

        //一般会登录的时候，记录 用户的最后一次登录时间
        //MQ 考虑使用mq 把信息发到mq当中，由mq的消费者 来去更新
        String clientType = ThreadContextHolder.getHttpRequest().getHeader("clientType");
        if(StringUtils.isBlank(clientType)) {
            clientType = "0";
        }
        ClientType type = ClientType.codeOf(Integer.parseInt(clientType));
        if(type == null){
            type = ClientType.UNKNOWN;
        }
        member.setClientEnum(type.getCode());
        member.setLastLoginDate(System.currentTimeMillis());
        this.memberMapper.updateById(member);

        Token token = genToken(member);
        return Result.success(token);
    }

    public static void main(String[] args) {
        System.out.println(new BCryptPasswordEncoder().encode("e10adc3949ba59abbe56e057f20f883e"));
    }
    private Token genToken(Member member) {
        //获取客户端类型
        String clientType = ThreadContextHolder.getHttpRequest().getHeader("clientType");
        Integer clientTypeInt = 0;
        try {
            if (StringUtils.isBlank(clientType)){clientType = "0";}
            clientTypeInt = Integer.parseInt(clientType);
            ClientType type = ClientType.codeOf(clientTypeInt);
            if (type != null){
                member.setClientEnum(clientTypeInt);
            }
        }catch (Exception e){
            e.printStackTrace();
            member.setClientEnum(ClientType.UNKNOWN.getCode());
        }
        member.setLastLoginDate(System.currentTimeMillis());
        this.memberMapper.updateById(member);


        AuthUser authUser = new AuthUser(member.getUsername(), String.valueOf(member.getId()),member.getNickName(), UserEnums.MEMBER);

        Token token = new Token();

        String jwtToken = TokenUtils.createToken(member.getUsername(), authUser, 7 * 24 * 60L);


        token.setAccessToken(jwtToken);
        redisTemplate.opsForValue().set(CachePrefix.ACCESS_TOKEN.name() + UserEnums.MEMBER.name() + jwtToken, "1",7, TimeUnit.DAYS);
        //设置刷新token，当accessToken过期的时候，可以通过refreshToken来 重新获取accessToken 而不用访问数据库


        String refreshToken = TokenUtils.createToken(member.getUsername(), authUser, 15 * 24 * 60L);


        redisTemplate.opsForValue().set(CachePrefix.REFRESH_TOKEN.name() + UserEnums.MEMBER.name() + jwtToken, "1",15, TimeUnit.DAYS);
        token.setRefreshToken(refreshToken);
        return token;
    }

    public MemberVo findMemberById(Long id) {
        LambdaQueryWrapper<Member> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Member::getId,id).eq(Member::getDisabled,false);
        Member member = memberMapper.selectOne(queryWrapper);
        return copy(member);
    }

    private MemberVo copy(Member member) {
        if (member == null){
            return null;
        }
        MemberVo memberVo = new MemberVo();
        BeanUtils.copyProperties(member,memberVo);
        memberVo.setId(String.valueOf(member.getId()));
        return memberVo;
    }
}
