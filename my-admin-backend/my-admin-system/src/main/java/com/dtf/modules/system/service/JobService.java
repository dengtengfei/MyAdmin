package com.dtf.modules.system.service;

import com.dtf.modules.system.domain.Job;
import com.dtf.modules.system.service.dto.JobDto;
import com.dtf.modules.system.service.dto.JobQueryCriteria;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 0 *
 * 1 * @Author:  deng.tengfei
 * 2 * @email:  imdtf@qq.com
 * 3 * @Date:  2021/6/27 0:38
 */
public interface JobService {
    /**
     * 创建岗位
     *
     * @param job \
     */
    void create(Job job);

    /**
     * 根据id列表删除
     *
     * @param ids \
     */
    void delete(Set<Long> ids);

    /**
     * 修改岗位
     *
     * @param job \
     */
    void update(Job job);

    /**
     * 查询全部岗位
     *
     * @param criteria \
     * @return \
     */
    List<JobDto> queryAll(JobQueryCriteria criteria);

    /**
     * 查询全部岗位
     *
     * @param criteria \
     * @param pageable \
     * @return \
     */
    Map<String, Object> queryAll(JobQueryCriteria criteria, Pageable pageable);

    /**
     * 导出岗位数据
     *
     * @param jobDtoList \
     * @param response   \
     * @throws IOException \
     */
    void download(List<JobDto> jobDtoList, HttpServletResponse response) throws IOException;

    /**
     * 验证是否可以删除
     *
     * @param ids \
     */
    void verification(Set<Long> ids);
}
