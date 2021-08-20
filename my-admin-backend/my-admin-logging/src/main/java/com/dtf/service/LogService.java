package com.dtf.service;

import com.dtf.domain.Log;
import com.dtf.service.dto.LogQueryCriteria;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 0 *
 * 1 * @Author:  deng.tengfei
 * 2 * @email:  imdtf@qq.com
 * 3 * @Date:  2021/7/12 22:05
 */
public interface LogService {
    /**
     * 保存日志
     *
     * @param username\
     * @param browser   \
     * @param ip        \
     * @param joinPoint \
     * @param log       \
     */
    @Async
    void save(String username, String browser, String ip, ProceedingJoinPoint joinPoint, Log log);

    /**
     * 删除所有提INFO日志
     */
    void deleteAllInfoLog();

    /**
     * 删除所有ERROR日志
     */
    void deleteAllErrorLog();

    /**
     * 查询全部日志
     *
     * @param criteria \
     * @return \
     */
    List<Log> queryAll(LogQueryCriteria criteria);

    /**
     * 分页查询全部日志
     *
     * @param criteria \
     * @param pageable \
     * @return \
     */
    Object queryAll(LogQueryCriteria criteria, Pageable pageable);

    /**
     * 查询所有用户日志
     *
     * @param criteria \
     * @param pageable \
     * @return \
     */
    Object queryUserLog(LogQueryCriteria criteria, Pageable pageable);

    /**
     * 查询异常详情
     * @param id \
     * @return \
     */
    Object findByErrDetail(Long id);

    /**
     * 导出日志数据
     * @param logList \
     * @param response \
     * @throws IOException \
     */
    void download(List<Log> logList, HttpServletResponse response) throws IOException;
}
