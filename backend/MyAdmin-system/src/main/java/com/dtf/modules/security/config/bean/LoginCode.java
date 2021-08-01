package com.dtf.modules.security.config.bean;

import lombok.Data;

/**
 * 0 *
 * 1 * @Author:  deng.tengfei
 * 2 * @email:  imdtf@qq.com
 * 3 * @Date:  2021/6/13 22:41
 */
@Data
public class LoginCode {
    private LoginCodeEnum codeType;

    /**
     * 验证码有效期 分钟
     */
    private Long expiration = 2L;

    /**
     * 验证码内容长度
     */
    private int length = 2;

    /**
     * 验证码宽度
     */
    private int width = 111;

    /**
     * 验证码长度
     */
    private int height = 36;

    /**
     * 验证码字体
     */
    private String fontName;

    /**
     * 字体大小
     */
    private int fontSize = 25;

    public LoginCodeEnum getCodeType() {
        return codeType;
    }
}
