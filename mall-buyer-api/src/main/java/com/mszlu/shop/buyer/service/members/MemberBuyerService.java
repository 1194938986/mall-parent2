package com.mszlu.shop.buyer.service.members;

import com.mszlu.shop.buyer.handler.security.UserContext;
import com.mszlu.shop.common.security.AuthUser;
import com.mszlu.shop.common.vo.Result;
import com.mszlu.shop.model.buyer.vo.member.MemberVo;
import com.mszlu.shop.sso.api.SSOApi;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

@Service
public class MemberBuyerService {

    @DubboReference(version = "1.0.0")
    private SSOApi ssoApi;

    public Result<MemberVo> getUserInfo() {
        AuthUser currentUser = UserContext.getCurrentUser();
        if (currentUser != null){
            String id = currentUser.getId();
            MemberVo memberById = ssoApi.findMemberById(Long.parseLong(id));
            return Result.success(memberById);
        }
        return Result.fail(-999,"登录已过期");
    }
}
