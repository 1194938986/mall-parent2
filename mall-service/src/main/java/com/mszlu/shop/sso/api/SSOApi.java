package com.mszlu.shop.sso.api;

import com.mszlu.shop.common.security.AuthUser;
import com.mszlu.shop.model.buyer.vo.member.MemberVo;

public interface SSOApi {
    MemberVo findMemberById(Long id);

    AuthUser checkToken(String token);
}
