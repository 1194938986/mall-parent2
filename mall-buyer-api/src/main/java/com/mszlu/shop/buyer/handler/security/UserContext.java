package com.mszlu.shop.buyer.handler.security;

import com.mszlu.shop.common.security.AuthUser;

/**
 * 用户上下文
 *
 */
public class UserContext {

    private static AuthenticationHandler authenticationHandler;

    public static void setHolder(AuthenticationHandler authenticationHandler) {
        UserContext.authenticationHandler = authenticationHandler;
    }

    public static AuthUser getCurrentUser() {
        return authenticationHandler.getAuthUser();
    }
}
