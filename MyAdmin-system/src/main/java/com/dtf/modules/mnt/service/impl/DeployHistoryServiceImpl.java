package com.dtf.modules.mnt.service.impl;

import com.dtf.modules.mnt.domain.DeployHistory;
import com.dtf.modules.mnt.repository.DeployHistoryRepository;
import com.dtf.modules.mnt.service.DeployHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 0 *
 * 1 * @Author:  deng.tengfei
 * 2 * @email:  imdtf@qq.com
 * 3 * @Date:  2021/7/15 21:16
 */
@Service
@RequiredArgsConstructor
public class DeployHistoryServiceImpl implements DeployHistoryService {
    private DeployHistoryRepository deployHistoryRepository;

    @Override
    public void create(DeployHistory deployHistory) {
        deployHistoryRepository.save(deployHistory);
    }
}
