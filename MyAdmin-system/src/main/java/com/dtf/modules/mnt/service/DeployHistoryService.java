package com.dtf.modules.mnt.service;

import com.dtf.modules.mnt.domain.DeployHistory;

/**
 * 0 *
 * 1 * @Author:  deng.tengfei
 * 2 * @email:  imdtf@qq.com
 * 3 * @Date:  2021/7/15 21:16
 */
public interface DeployHistoryService {
    /**
     * 创建部署历史
     * @param deployHistory
     */
    void create(DeployHistory deployHistory);
}
