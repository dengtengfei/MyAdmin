package com.dtf.modules.security.service;

import com.dtf.utils.StringUtils;
import org.springframework.stereotype.Component;

/**
 * 0 *
 * 1 * @Author:  deng.tengfei
 * 2 * @email:  imdtf@qq.com
 * 3 * @Date:  2021/6/13 20:16
 */
@Component
public class UserCacheClean {
    /**
     * 清理特定用户缓存信息
     * @param userName
     */
    public void cleanUserCache(String userName) {
        if (StringUtils.isNotBlank(userName)) {
            UserDetailsServiceImpl.userDtoCache.remove(userName);
        }
    }
}
