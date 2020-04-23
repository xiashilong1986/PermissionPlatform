package com.permission.utils.global.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * http返回值
 * created by xiashilong
 * 2017-11-23 15:54
 */
@Getter
@AllArgsConstructor
public enum ResultEnum {
    FAILURE(100, "FAILURE"),
    WRONG_LOGIN(100, "账号或密码错误"),
    ACCOUNT_DOES_NOT_EXIST(100, "账号不存在"),
    WRONG_PASSWORD(100, "密码错误"),
    ACCOUNT_EXIST(100, "账号已存在"),
    LOGIN_FAIL(100, "登陆失败,请稍后重试"),
    PERMISSION_ERROR(100, "权限不足,请联系管理员"),
    ACCOUNT_LOCKED(100, "账号已冻结,请联系管理员"),
    ACCESS_FREQUENTLY(100, "系统繁忙,请稍后再试"),
    NULL_DATA(101, "暂无数据"),


    TOKEN_ERROR(501, "token错误!"),
    IP_ERROR(501, "ip异常,请重新登陆"),

    PERMISSION_CHANGE(401, "密码或权限变更,请重新登陆!"),
    TOKEN_INVALID(402, "token失效,请重新获取token"),
    SESSION_ERROR(403, "该账号已在其他设备上登陆,如非本人操作,请修改密码!"),


    LOGIN_SUCCESS(200, "登陆成功"),
    LOGOUT_SUCCESS(200, "登出成功"),
    SUCCESS(200, "SUCCESS"),

    UNKNOWN_TYPE(500, "Unknown type"),
    UNKNOWN_ERROR(500, "System error"),
    UNABLE_TO_DELETE(500, "该角色已绑定用户,无法删除"),
    TO_BIG_FILE(500, "File is too large"),
    NO_FILE(500, "File is null");


    private Integer code;

    private String msg;
}
