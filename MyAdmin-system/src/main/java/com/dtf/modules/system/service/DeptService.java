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
     * 创建
     * @param dept \
     */
    void create(Dept dept);

    /**
     * 删除部门
     * @param deptDtoSet \
     */
    void delete(Set<DeptDto> deptDtoSet);

    /**
     * 获取待删除的部门
     * @param deptList \
     * @param deptDtoSet \
     * @return \
     */
    Set<DeptDto> getDeleteDeptLst(List<Dept> deptList, Set<DeptDto> deptDtoSet);

    /**
     * 更新部门
     * @param dept \
     */
    void update(Dept dept);

    /**
     * 查询全部
     * @param criteria \
     * @param isQuery \
     * @return \
     * @throws IllegalAccessException \
     */
    List<DeptDto> queryAll(DeptQueryCriteria criteria, Boolean isQuery) throws IllegalAccessException;

    /**
     * 根据id查询Dto
     * @param id \
     * @return \
     */
    DeptDto findById(Long id);

    /**
     * 根据PID查询
     * @param pid \
     * @return \
     */
    List<Dept> findByPid(long pid);

    /**
     * 根据角色 ID 查询
     * @param id \
     * @return \
     */
    Set<Dept> findByRoleId(Long id);

    /**
     * 获取子部门id列表
     * @param deptList \
     * @return \
     */
    List<Long> getDeptChildren(List<Dept> deptList);

    /**
     * 获取同级与上级数据
     * @param deptDto \
     * @param deptList \
     * @return \
     */
    List<DeptDto> getSuperior(DeptDto deptDto, List<Dept> deptList);

    /**
     * 构建部门树
     * @param deptDtoList
     * @return
     */
    Object buildTree(List<DeptDto> deptDtoList);

    /**
     * 验证是否被角色或者用户关联
     * @param deptDtoSet \
     */
    void verification(Set<DeptDto> deptDtoSet);
}
