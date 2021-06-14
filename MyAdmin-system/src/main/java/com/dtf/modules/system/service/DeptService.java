package com.dtf.modules.system.service;

import com.dtf.modules.system.domain.Dept;
import com.dtf.modules.system.service.dto.DeptDto;
import com.dtf.modules.system.service.dto.DeptQueryCriteria;

import java.util.List;
import java.util.Set;

/**
 * 0 *
 * 1 * @Author:  deng.tengfei
 * 2 * @email:  imdtf@qq.com
 * 3 * @Date:  2021/6/12 0:04
 */
public interface DeptService {
    /**
     * 查询全部
     * @param criteria 条件
     * @param isQuery 是否查询
     * @return
     */
    List<DeptDto> queryAll(DeptQueryCriteria criteria, Boolean isQuery) throws IllegalAccessException;

    /**
     * 根据PID查询
     * @param pid
     * @return
     */
    List<Dept> findByPid(long pid);

    /**
     * 创建
     * @param dept
     */
    void create(Dept dept);

    /**
     * 根据角色 ID 查询
     * @param id
     * @return
     */
    Set<Dept> findByRoleId(Long id);

    /**
     * 获取子部门id列表
     * @param deptList
     * @return
     */
    List<Long> getDeptChildren(List<Dept> deptList);
}
