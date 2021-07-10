package com.dtf.modules.quartz.service.impl;

import cn.hutool.core.util.IdUtil;
import com.dtf.exception.BadRequestException;
import com.dtf.modules.quartz.domain.QuartzJob;
import com.dtf.modules.quartz.repository.QuartzJobRepository;
import com.dtf.modules.quartz.repository.QuartzLogRepository;
import com.dtf.modules.quartz.service.QuartzJobService;
import com.dtf.modules.quartz.service.dto.JobsQueryCriteria;
import com.dtf.modules.quartz.utils.QuartzManage;
import com.dtf.utils.*;
import lombok.RequiredArgsConstructor;
import org.quartz.CronExpression;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * 0 *
 * 1 * @Author:  deng.tengfei
 * 2 * @email:  imdtf@qq.com
 * 3 * @Date:  2021/7/10 20:50
 */
@Service
@RequiredArgsConstructor
public class QuartzJobServiceImpl implements QuartzJobService {
    private final QuartzJobRepository quartzJobRepository;
    private final QuartzLogRepository quartzLogRepository;
    private final QuartzManage quartzManage;
    private final RedisUtils redisUtils;

    @Override
    public void create(QuartzJob quartzJob) {
        if (!CronExpression.isValidExpression(quartzJob.getCronExpression())) {
            throw new BadRequestException("Cron表达式错误");
        }
        // TODO verify quartzJob.getId()
        quartzJobRepository.save(quartzJob);
        quartzManage.addJob(quartzJob);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Set<Long> ids) {
        for (Long id : ids) {
            QuartzJob quartzJob = findById(id);
            quartzManage.delete(quartzJob);
            quartzJobRepository.delete(quartzJob);
        }
    }

    @Override
    public void update(QuartzJob quartzJob) {
        if (!CronExpression.isValidExpression(quartzJob.getCronExpression())) {
            throw new BadRequestException("Cron表达式错误");
        }
        if (StringUtils.isNotBlank(quartzJob.getSubTask())) {
            List<String> subTasks = Arrays.asList(quartzJob.getSubTask().split("[,，]"));
            if (subTasks.contains(quartzJob.getId().toString())) {
                throw new BadRequestException("子任务不能添加当前任务ID");
            }
        }
        // TODO verify quartzJob.getId()
        quartzJobRepository.save(quartzJob);
        quartzManage.updateJobCron(quartzJob);
    }

    @Override
    public void updateIsPause(QuartzJob quartzJob) {
        if (quartzJob.getIsPause()) {
            quartzManage.resumeJob(quartzJob);
            quartzJob.setIsPause(false);
        } else {
            quartzManage.pauseJob(quartzJob);
            quartzJob.setIsPause(true);
        }
        quartzJobRepository.save(quartzJob);
    }

    @Override
    public QuartzJob findById(Long id) {
        QuartzJob quartzJob = quartzJobRepository.findById(id).orElseGet(QuartzJob::new);
        ValidationUtil.isNull(quartzJob.getId(), "QuartzJob", "id", id);
        return quartzJob;
    }

    @Override
    public List<QuartzJob> queryAll(JobsQueryCriteria criteria) {
        return quartzJobRepository.findAll(((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder)));
    }

    @Override
    public Object queryAll(JobsQueryCriteria criteria, Pageable pageable) {
        return PageUtil.toPage(quartzJobRepository.findAll(((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder)), pageable));
    }

    @Override
    public void download(List<QuartzJob> quartzJobList, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (QuartzJob quartzJob : quartzJobList) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("任务名称", quartzJob.getJobName());
            map.put("Bean名称", quartzJob.getBeanName());
            map.put("执行方法", quartzJob.getMethodName());
            map.put("参数", quartzJob.getParams());
            map.put("表达式", quartzJob.getCronExpression());
            map.put("状态", quartzJob.getIsPause() ? "暂停中" : "运行中");
            map.put("描述", quartzJob.getDescription());
            map.put("创建日期", quartzJob.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public void executeSubJob(String[] tasks) throws InterruptedException {
        for (String id : tasks) {
            QuartzJob quartzJob = findById(Long.parseLong(id));
            String uuid = IdUtil.simpleUUID();
            quartzJob.setUuid(uuid);
            execute(quartzJob);
            Boolean result = (Boolean) redisUtils.get(uuid);
            while (result == null) {
                Thread.sleep(5000);
                result = (Boolean) redisUtils.get(uuid);
            }
            if (!result) {
                redisUtils.del(uuid);
                break;
            }
        }
    }

    @Override
    public void execute(QuartzJob quartzJob) {
        quartzManage.runJobNow(quartzJob);
    }
}
