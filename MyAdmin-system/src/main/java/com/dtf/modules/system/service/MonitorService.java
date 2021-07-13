package com.dtf.modules.system.service;

import java.util.Map;

/**
 * 0 *
 * 1 * @Author:  deng.tengfei
 * 2 * @email:  imdtf@qq.com
 * 3 * @Date:  2021/7/13 22:16
 */
public interface MonitorService {
    /**
     * 查询服务监控详情
     * @return \
     */
    Map<String, Object> getServers();
}
