package com.dtf.modules.security.config.bean;

import lombok.Data;

/**
 * 0 *
 * 1 * @Author:  deng.tengfei
 * 2 * @email:  imdtf@qq.com
 * 3 * @Date:  2021/6/13 22:27
 */
@Data
public class SecurityProperties {
    /**
     * Request Headers: Authorization
     */
    private String header;

    /**
     * 令牌前缀，最后六个空格 Bearer
     */
    private String tokenStartWith;

    /**
     * 必须使用最少88位的Base64对该令牌进行编码
     */
    private String base64Secret;

    /**
     * 令牌过期时间 单位为毫秒
     */
    private Long tokenValidityInSeconds;

    /**
     * 在线用户key：根据key查询redis中在线用户的数据
     */
    private String onlineKey;

    /**
     * 验证码 key
     */
    private String codeKey;

    /**
     * token 续期检查
     */
    private Long detect;

    /**
     * 续期时间
     */
    private Long renew;

    public String getTokenStartWith() {
        return tokenStartWith + " ";
    }
}
