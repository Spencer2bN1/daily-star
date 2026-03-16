package com.dailystar.component;

import com.dailystar.model.LoginUser;

public final class AuthContextHolder {

    private static final ThreadLocal<LoginUser> CONTEXT = new ThreadLocal<LoginUser>();

    private AuthContextHolder() {
    }

    public static void set(LoginUser loginUser) {
        CONTEXT.set(loginUser);
    }

    public static LoginUser get() {
        return CONTEXT.get();
    }

    public static Long requireAccountId() {
        LoginUser loginUser = CONTEXT.get();
        return loginUser == null ? null : loginUser.getAccountId();
    }

    public static void clear() {
        CONTEXT.remove();
    }
}
