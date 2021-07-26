package com.dtf.modules.mnt.repository;

import com.dtf.modules.mnt.domain.DeployHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Set;

/**
 * 0 *
 * 1 * @Author:  deng.tengfei
 * 2 * @email:  imdtf@qq.com
 * 3 * @Date:  2021/7/15 21:15
 */
public interface DeployHistoryRepository extends JpaRepository<DeployHistory, String>, JpaSpecificationExecutor<DeployHistory> {
    /**
     * 根据id删除
     * @param ids \
     */
    void deleteAllByIdIn(Set<String> ids);
}
