package com.dtf.modules.system.service;

import com.dtf.modules.system.domain.Dept;
import com.dtf.modules.system.service.dto.DeptDto;
import com.dtf.modules.system.service.dto.DeptQueryCriteria;

import java.util.List;

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
     * 创建
     * @param dept
     */
    void create(Dept dept);
}
