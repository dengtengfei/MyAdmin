package com.dtf.modules.quartz.utils;

import com.dtf.config.ThreadPoolExecutorUtil;
import com.dtf.modules.quartz.domain.QuartzJob;
import com.dtf.modules.quartz.domain.QuartzLog;
import com.dtf.modules.quartz.repository.QuartzLogRepository;
import com.dtf.modules.quartz.service.QuartzJobService;
import com.dtf.utils.RedisUtils;
import com.dtf.utils.SpringContextHolder;
import com.dtf.utils.StringUtils;
import com.dtf.utils.ThrowableUtil;
import org.quartz.JobExecutionContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 0 *
 * 1 * @Author:  deng.tengfei
 * 2 * @email:  imdtf@qq.com
 * 3 * @Date:  2021/7/10 21:33
 */
@Async
public class ExecutionJob extends QuartzJobBean {
    private static final ThreadPoolExecutor EXECUTOR = ThreadPoolExecutorUtil.getPool();

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) {
        QuartzJob quartzJob = (QuartzJob) jobExecutionContext.getMergedJobDataMap().get(QuartzJob.JOB_KEY);
        QuartzLogRepository quartzLogRepository = SpringContextHolder.getBean(QuartzLogRepository.class);
        QuartzJobService quartzJobService = SpringContextHolder.getBean(QuartzJobService.class);
        RedisUtils redisUtils = SpringContextHolder.getBean(RedisUtils.class);

        String uuid = quartzJob.getUuid();
        QuartzLog log = new QuartzLog();
        log.setJobName(quartzJob.getJobName());
        log.setBeanName(quartzJob.getBeanName());
        log.setMethodName(quartzJob.getMethodName());
        log.setParams(quartzJob.getParams());
        log.setCronExpression(quartzJob.getCronExpression());
        long startTime = System.currentTimeMillis();
        try {
            System.out.println("-----------------------------------------------");
            System.out.println("任务开始执行, 名称: " + quartzJob.getJobName());
            QuartzRunnable task = new QuartzRunnable(quartzJob.getBeanName(), quartzJob.getMethodName(), quartzJob.getParams());
            Future<?> future = EXECUTOR.submit(task);
            future.get();
            long times = System.currentTimeMillis() - startTime;
            log.setTime(times);
            if (StringUtils.isNotBlank(uuid)) {
                redisUtils.set(uuid, true);
            }
            log.setIsSuccess(true);
            System.out.println("任务执行成功, 名称: " + quartzJob.getJobName() + ", 执行用时: " + times + " ms");
            System.out.println("-----------------------------------------------");
            if (StringUtils.isNotBlank(quartzJob.getSubTask())) {
                String[] subTasks = quartzJob.getSubTask().split("[,，]");
                quartzJobService.executeSubJob(subTasks);
            }
        } catch (Exception e) {
            if (StringUtils.isNotBlank(uuid)) {
                redisUtils.set(uuid, false);
            }
            System.out.println("任务执行失败, 名称: " + quartzJob.getJobName());
            System.out.println("-----------------------------------------------");
            long times = System.currentTimeMillis() - startTime;
            log.setTime(times);
            log.setIsSuccess(false);
            log.setExceptionDetail(ThrowableUtil.getStackTrace(e));
            if (quartzJob.getPauseAfterFailure() != null && quartzJob.getPauseAfterFailure()) {
                quartzJob.setIsPause(false);
                quartzJobService.updateIsPause(quartzJob);
            }
            if (quartzJob.getEmail() != null) {
                // TODO email
            }
        } finally {
            quartzLogRepository.save(log);
        }
    }
}
