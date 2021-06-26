package com.dtf.modules.system.service;

import com.dtf.modules.system.service.dto.JobQueryCriteria;
import org.springframework.data.domain.Pageable;

import java.util.Map;

/**
 * 0 *
 * 1 * @Author:  deng.tengfei
 * 2 * @email:  imdtf@qq.com
 * 3 * @Date:  2021/6/27 0:38
 */
public interface JobService {
    /**
     * 查询全部岗位
     * @param criteria
     * @param pageable
     * @return
     */
    Map<String,Object> queryAll(JobQueryCriteria criteria, Pageable pageable);
}
