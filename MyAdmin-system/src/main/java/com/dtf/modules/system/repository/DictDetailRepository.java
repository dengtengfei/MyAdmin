package com.dtf.modules.system.repository;

import com.dtf.modules.system.domain.DictDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * 0 *
 * 1 * @Author:  deng.tengfei
 * 2 * @email:  imdtf@qq.com
 * 3 * @Date:  2021/6/24 22:29
 */
public interface DictDetailRepository extends JpaRepository<DictDetail, Long>, JpaSpecificationExecutor<DictDetail> {
    // TODO DictDetail 中没有DictName字段???

    /**
     * 根据字典名称查询
     * @param name
     * @return
     */
    List<DictDetail> findByDictName(String name);
}
