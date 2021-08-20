package com.dtf.modules.quartz.utils;

import com.dtf.exception.BadRequestException;
import com.dtf.modules.quartz.domain.QuartzJob;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 0 *
 * 1 * @Author:  deng.tengfei
 * 2 * @email:  imdtf@qq.com
 * 3 * @Date:  2021/7/10 21:31
 */
@Component
@Slf4j
public class QuartzManage {
    private static final String JOB_NAME = "TASK_";
    @Resource(name = "scheduler")
    private Scheduler scheduler;

    public void addJob(QuartzJob quartzJob) {
        try {
            JobDetail jobDetail = JobBuilder.newJob(ExecutionJob.class).withIdentity(JOB_NAME + quartzJob.getId()).build();
            // 通过触发器名和cron表达式创建Trigger
            // TODO TriggerBuilder.newTrigger() ???
            Trigger cronTrigger = TriggerBuilder.newTrigger().withIdentity(JOB_NAME + quartzJob.getId()).
                    startNow().withSchedule(CronScheduleBuilder.cronSchedule(quartzJob.getCronExpression())).build();

            cronTrigger.getJobDataMap().put(QuartzJob.JOB_KEY, quartzJob);

            // 重置启动时间
            ((CronTriggerImpl) cronTrigger).setStartTime(new Date());

            // 执行定时任务
            scheduler.scheduleJob(jobDetail, cronTrigger);

            if (quartzJob.getIsPause()) {
                pauseJob(quartzJob);
            }
        } catch (Exception e) {
            log.error("创建定时任务失败", e);
            throw new BadRequestException("创建定时任务失败: " + e.getMessage());
        }
    }

    public void runJobNow(QuartzJob quartzJob) {
        try {
            TriggerKey triggerKey = TriggerKey.triggerKey(JOB_NAME + quartzJob.getId());
            CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
            if (trigger == null) {
                addJob(quartzJob);
            }
            JobDataMap dataMap = new JobDataMap();
            dataMap.put(QuartzJob.JOB_KEY, quartzJob);
            JobKey jobKey = JobKey.jobKey(JOB_NAME + quartzJob.getId());
            scheduler.triggerJob(jobKey, dataMap);
        } catch (Exception e) {
            log.error("执行定时任务失败", e);
            throw new BadRequestException("执行定时任务失败: " + e.getMessage());
        }
    }

    public void delete(QuartzJob quartzJob) {
        try {
            JobKey jobKey = JobKey.jobKey(JOB_NAME + quartzJob.getId());
            scheduler.pauseJob(jobKey);
            scheduler.deleteJob(jobKey);
        } catch (Exception e) {
            log.error("删除定时任务失败", e);
            throw new BadRequestException("删除定时任务失败: " + e.getMessage());
        }
    }

    public void updateJobCron(QuartzJob quartzJob) {
        try {
            TriggerKey triggerKey = TriggerKey.triggerKey(JOB_NAME + quartzJob.getId());
            CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
            if (trigger == null) {
                addJob(quartzJob);
                trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
            }
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(quartzJob.getCronExpression());
            trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();
            ((CronTriggerImpl) trigger).setStartTime(new Date());
            trigger.getJobDataMap().put(QuartzJob.JOB_KEY, quartzJob);

            scheduler.rescheduleJob(triggerKey, trigger);
            if (quartzJob.getIsPause()) {
                pauseJob(quartzJob);
            }
        } catch (Exception e) {
            log.error("更新定时任务失败", e);
            throw new BadRequestException("更新定时任务失败: " + e.getMessage());
        }
    }

    public void resumeJob(QuartzJob quartzJob) {
        try {
            TriggerKey triggerKey = TriggerKey.triggerKey(JOB_NAME + quartzJob.getId());
            CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
            if (trigger == null) {
                addJob(quartzJob);
            }
            JobKey jobKey = JobKey.jobKey(JOB_NAME + quartzJob.getId());
            scheduler.resumeJob(jobKey);
        } catch (Exception e) {
            log.error("恢复定时任务失败", e);
            throw new BadRequestException("恢复定时任务失败: " + e.getMessage());
        }
    }

    public void pauseJob(QuartzJob quartzJob) {
        try {
            JobKey jobKey = JobKey.jobKey(JOB_NAME + quartzJob.getId());
            scheduler.pauseJob(jobKey);
        } catch (Exception e) {
            log.error("暂停定时任务失败", e);
            throw new BadRequestException("暂停定时任务失败: " + e.getMessage());
        }
    }
}
