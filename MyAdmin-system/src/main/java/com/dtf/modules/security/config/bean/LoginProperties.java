package com.dtf.modules.security.config.bean;

/**
 * 0 *
 * 1 * @Author:  deng.tengfei
 * 2 * @email:  imdtf@qq.com
 * 3 * @Date:  2021/6/13 22:40
 */
public class LoginProperties {

    /**
     * 账号单用户 登录
     */
    private boolean singleLogin = false;

    /**
     * 验证码信息
     */
    private LoginCode loginCode;

    /**
     * 用户登录信息缓存
     */
    private boolean cacheEnabled;

    public boolean isSingleLogin() {
        return singleLogin;
    }

    public boolean isCacheEnabled() {
        return cacheEnabled;
    }
}
