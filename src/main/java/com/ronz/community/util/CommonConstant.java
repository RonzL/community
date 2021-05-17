package com.ronz.community.util;

/**
 * 账号激活状态枚举类
 */
public enum CommonConstant {

    ACTIVATION_FAILURE(0, "您的账号激活失败，请重新激活！"),
    ACTIVATION_SUCCESS(1, "您的账号激活成功！"),
    ACTIVATION_REPEAT(2, "您的账号已经激活，请勿重复激活！"),

    DEFAULT_EXPIRED_SECONDS(3600 * 10, "凭证有效期为 10 小时"),
    REMEMBER_EXPIRED_SECONDS(3600 * 24 * 100, "凭证有效期为 100 天"),

    LOGIN_TICKED_VALID(0, "登录凭证有效！"),
    LOGIN_TICKED_INVALID(1, "登录凭证无效！");

    private int code;
    private String msg;

    CommonConstant(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
