package com.dtf.modules.system.repository;

import com.dtf.modules.system.domain.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Set;

/**
 * 0 *
 * 1 * @Author:  deng.tengfei
 * 2 * @email:  imdtf@qq.com
 * 3 * @Date:  2021/6/27 0:40
 */
public interface JobRepository extends JpaRepository<Job, Long>, JpaSpecificationExecutor<Job> {
    /**
     * 根据id列表删除
     *
     * @param ids \
     */
    void deleteAllByIdIn(Set<Long> ids);

    /**
     * 根据岗位名称查询
     *
     * @param name \
     * @return \
     */
    Job findByName(String name);
}
