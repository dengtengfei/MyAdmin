package com.dtf.modules.system.service.impl;

import com.dtf.exception.BadRequestException;
import com.dtf.exception.EntityExistException;
import com.dtf.modules.system.domain.Job;
import com.dtf.modules.system.repository.JobRepository;
import com.dtf.modules.system.repository.UserRepository;
import com.dtf.modules.system.service.JobService;
import com.dtf.modules.system.service.dto.JobDto;
import com.dtf.modules.system.service.dto.JobQueryCriteria;
import com.dtf.modules.system.service.mapstruct.JobMapper;
import com.dtf.utils.*;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
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
 * 3 * @Date:  2021/6/27 0:39
 */
@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "job")
public class JobServiceImpl implements JobService {
    private final JobRepository jobRepository;
    private final JobMapper jobMapper;
    private final UserRepository userRepository;
    private final RedisUtils redisUtils;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(Job job) {
        if (jobRepository.findByName(job.getName()) != null) {
            throw new EntityExistException(Job.class, "name", job.getName());
        }
        jobRepository.save(job);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Set<Long> ids) {
        jobRepository.deleteAllByIdIn(ids);
        redisUtils.delByKeys(CacheKey.JOB_ID, ids);
    }

    @Override
    @CacheEvict(key = "'id:' + #p0.id")
    @Transactional(rollbackFor = Exception.class)
    public void update(Job job) {
        Job oldJob = jobRepository.findById(job.getId()).orElseGet(Job::new);
        Job jobByName = jobRepository.findByName(job.getName());
        if (jobByName != null && !oldJob.getId().equals(jobByName.getId())) {
            throw new EntityExistException(Job.class, "name", job.getName());
        }
        // 没找到job的id对应记录会抛异常
        ValidationUtil.isNull(oldJob.getId(), "Job", "id", job.getId());
        jobRepository.save(job);
    }

    @Override
    public List<JobDto> queryAll(JobQueryCriteria criteria) {
        return jobMapper.toDto(jobRepository.findAll(((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder))));
    }

    @Override
    public Map<String, Object> queryAll(JobQueryCriteria criteria, Pageable pageable) {
        Page<Job> page = jobRepository.findAll(((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder)), pageable);
        return PageUtil.toPage(page.map(jobMapper::toDto).getContent(), page.getTotalElements());
    }

    @Override
    public void download(List<JobDto> jobDtoList, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (JobDto jobDto : jobDtoList) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("岗位名称", jobDto.getName());
            map.put("岗位状态", jobDto.getEnabled() ? "启用" : "停用");
            map.put("创建日期", jobDto.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public void verification(Set<Long> ids) {
        if (userRepository.countByJobs(ids) > 0) {
            throw new BadRequestException("所选岗位中存在关联用户，请解除关联后再试");
        }
    }
}
