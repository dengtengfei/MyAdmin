package com.dtf.utils.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 0 *
 * 1 * @Author:  deng.tengfei
 * 2 * @email:  imdtf@qq.com
 * 3 * @Date:  2021/6/27 12:45
 */
@Getter
@AllArgsConstructor
public enum CodeEnum {
    /* 通过手机号码重置邮箱 */
    PHONE_RESET_EMAIL_CODE("phone_reset_email_code_", "通过手机号码重置邮箱"),

    /* 通过旧邮箱重置邮箱 */
    EMAIL_RESET_EMAIL_CODE("email_reset_email_code_", "通过旧邮箱重置邮箱"),

    /* 通过手机号码重置密码 */
    PHONE_RESET_PWD_CODE("phone_reset_pwd_code_", "通过手机号码重置密码"),

    /* 通过邮箱重置密码 */
    EMAIL_RESET_PWD_CODE("email_reset_pwd_code_", "通过邮箱重置密码");

    private final String key;
    private final String description;
}
