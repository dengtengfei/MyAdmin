package com.dtf.modules.system.service;

/**
 * 0 *
 * 1 * @Author:  deng.tengfei
 * 2 * @email:  imdtf@qq.com
 * 3 * @Date:  2021/6/25 23:58
 */
public interface VerifyService {
    /**
     * 验证
     * @param key \
     * @param code \
     */
    void validated(String key, String code);
}
