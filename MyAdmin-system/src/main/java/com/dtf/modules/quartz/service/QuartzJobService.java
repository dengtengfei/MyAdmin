package com.dtf.modules.quartz.service;

import com.dtf.modules.quartz.domain.QuartzJob;
import com.dtf.modules.quartz.service.dto.JobsQueryCriteria;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * 0 *
 * 1 * @Author:  deng.tengfei
 * 2 * @email:  imdtf@qq.com
 * 3 * @Date:  2021/7/10 20:49
 */
public interface QuartzJobService {
    /**
     * 创建定时任务
     *
     * @param quartzJob \
     */
    void create(QuartzJob quartzJob);

    /**
     * 删除定时任务
     *
     * @param ids \
     */
    void delete(Set<Long> ids);

    /**
     * 修改定时任务
     *
     * @param quartzJob \
     */
    void update(QuartzJob quartzJob);

    /**
     * 更改定时任务状态
     *
     * @param quartzJob \
     */
    void updateIsPause(QuartzJob quartzJob);

    /**
     * 根据id查找
     *
     * @param id \
     * @return \
     */
    QuartzJob findById(Long id);

    /**
     * 查询全部定时任务
     *
     * @param criteria \
     * @return \
     */
    List<QuartzJob> queryAll(JobsQueryCriteria criteria);

    /**
     * 查询全部定时任务
     *
     * @param criteria \
     * @param pageable \
     * @return \
     */
    Object queryAll(JobsQueryCriteria criteria, Pageable pageable);

    /**
     * 导出定时任务数据
     *
     * @param quartzJobList \
     * @param response      \
     * @throws IOException \
     */
    void download(List<QuartzJob> quartzJobList, HttpServletResponse response) throws IOException;

    /**
     * 执行任务
     *
     * @param tasks \
     * @throws InterruptedException \
     */
    void executeSubJob(String[] tasks) throws InterruptedException;

    /**
     * 执行Job
     *
     * @param quartzJob \
     */
    void execute(QuartzJob quartzJob);
}
