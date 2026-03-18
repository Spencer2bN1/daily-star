package com.dailystar.enums;

import lombok.Getter;

@Getter
public enum MessageCodeEnum {

    SUCCESS(0, "成功"),
    PARAM_ERROR(1001, "请求参数错误"),
    NOT_FOUND(1002, "请求资源不存在"),
    INTERNAL_ERROR(1003, "服务内部异常"),
    UNAUTHORIZED(2001, "请先登录"),
    TOKEN_INVALID(2002, "登录状态已失效，请重新登录"),
    MOBILE_EXISTS(2003, "手机号已注册"),
    ACCOUNT_NOT_FOUND(2004, "账号不存在"),
    ACCOUNT_DISABLED(2005, "账号不可用"),
    PASSWORD_ERROR(2006, "手机号或密码错误"),
    USER_NOT_FOUND(2007, "用户不存在"),
    PROFILE_INCOMPLETE(2008, "请先完善基础信息"),
    FOLLOW_SELF_NOT_ALLOWED(2009, "不能关注自己");

    private final int code;
    private final String message;

    MessageCodeEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
