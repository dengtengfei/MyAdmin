package com.dtf.repository;

import com.dtf.domain.Log;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;

/**
 * 0 *
 * 1 * @Author:  deng.tengfei
 * 2 * @email:  imdtf@qq.com
 * 3 * @Date:  2021/7/12 22:08
 */
public interface LogRepository extends JpaRepository<Log, Long>, JpaSpecificationExecutor<Log> {
    /**
     * 根据日志类型删除
     * @param logType \
     */
    @Modifying
    void deleteAllByLogType(String logType);
}
