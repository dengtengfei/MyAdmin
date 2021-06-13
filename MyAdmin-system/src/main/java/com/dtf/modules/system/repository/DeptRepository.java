package com.dtf.modules.system.repository;

import com.dtf.modules.system.domain.Dept;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;

/**
 * 0 *
 * 1 * @Author:  deng.tengfei
 * 2 * @email:  imdtf@qq.com
 * 3 * @Date:  2021/6/12 23:01
 */
public interface DeptRepository extends JpaRepository<Dept, Long>, JpaSpecificationExecutor<Dept> {
    /**
     * 计算子节点个数
     * @param pid 节点id
     * @return
     */
    int countByPid(Long pid);

    /**
     * 更新部门子节点数量
     * @param count 数量
     * @param id 部门id
     */
    @Modifying
    @Query(value = "update sys_dept set sub_count = ?1 where id = ?2", nativeQuery = true)
    void updateSubCntById(Integer count, Long id);
}
