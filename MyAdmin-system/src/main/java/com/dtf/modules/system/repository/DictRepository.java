package com.dtf.modules.system.repository;

import com.dtf.modules.system.domain.Dict;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Set;

/**
 * 0 *
 * 1 * @Author:  deng.tengfei
 * 2 * @email:  imdtf@qq.com
 * 3 * @Date:  2021/6/24 22:27
 */
public interface DictRepository extends JpaRepository<Dict, Long>, JpaSpecificationExecutor<Dict> {
    /**
     * 根据id列表删除
     *
     * @param ids \
     */
    void deleteByIdIn(Set<Long> ids);

    /**
     * 根据id列表查询
     *
     * @param ids \
     * @return \
     */
    List<Dict> findByIdIn(Set<Long> ids);

    /**
     * 根据字典名称查询
     *
     * @param name \
     * @return \
     */
    Dict findByName(String name);
}
