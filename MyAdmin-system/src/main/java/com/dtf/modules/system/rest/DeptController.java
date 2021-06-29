package com.dtf.modules.system.rest;

import cn.hutool.core.collection.CollectionUtil;
import com.dtf.annotation.Log;
import com.dtf.base.BaseEntity;
import com.dtf.exception.BadRequestException;
import com.dtf.modules.system.domain.Dept;
import com.dtf.modules.system.service.DeptService;
import com.dtf.modules.system.service.dto.DeptDto;
import com.dtf.modules.system.service.dto.DeptQueryCriteria;
import com.dtf.utils.PageUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * 0 *
 * 1 * @Author:  deng.tengfei
 * 2 * @email:  imdtf@qq.com
 * 3 * @Date:  2021/6/10 22:53
 */
@RestController
@RequiredArgsConstructor
@Api(tags = "系统：部门管理")
@RequestMapping("/api/dept")
public class DeptController {
    private final DeptService deptService;

    /**
     * 部门
     */
    private static final String ENTITY_NAME = "dept";

    @Log("新增部门")
    @ApiOperation("新增部门")
    @PostMapping
    @PreAuthorize("@dtf.check('dept:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody Dept dept) {
        if (dept.getId() != null) {
            throw new BadRequestException("A new " + ENTITY_NAME + "cannot created, because it has an id.");
        }
        deptService.create(dept);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Log("删除部门")
    @ApiOperation("删除部门")
    @DeleteMapping
    @PreAuthorize("@dtf.check('dept:del')")
    public ResponseEntity<Object> delete(@RequestBody Set<Long> ids) {
        Set<DeptDto> deptDtoSet = new HashSet<>();
        for (Long id : ids) {
            List<Dept> deptChildren = deptService.findByPid(id);
            deptDtoSet.add(deptService.findById(id));
            if (CollectionUtil.isNotEmpty(deptChildren)) {
                deptDtoSet = deptService.getDeleteDeptLst(deptChildren, deptDtoSet);
            }
        }
        deptService.verification(deptDtoSet);
        deptService.delete(deptDtoSet);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Log("修改部门")
    @ApiOperation("修改部门")
    @PutMapping
    @PreAuthorize("@dtf.check('dept:edit')")
    public ResponseEntity<Object> update(@Validated(BaseEntity.Update.class) @RequestBody Dept dept) {
        deptService.update(dept);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation("查询部门")
    @GetMapping
    @PreAuthorize("@dtf.check('user:list','dept:list')")
    public ResponseEntity<Object> query(DeptQueryCriteria criteria) throws Exception {
        List<DeptDto> deptDtoList = deptService.queryAll(criteria, true);
        return new ResponseEntity<>(PageUtil.toPage(deptDtoList, deptDtoList.size()), HttpStatus.OK);
    }

    @ApiOperation("查询部门: 根据ID获取同级与上级数据")
    @PostMapping("/superior")
    @PreAuthorize("@dtf.check('user:list','dept:list')")
    public ResponseEntity<Object> getSuperior(@RequestBody List<Long> ids) {
        Set<DeptDto> deptDtoSet = new LinkedHashSet<>();
        for (Long id : ids) {
            DeptDto deptDto = deptService.findById(id);
            List<DeptDto> deptDtoList = deptService.getSuperior(deptDto, new ArrayList<>());
            deptDtoSet.addAll(deptDtoList);
        }
        return new ResponseEntity<>(deptService.buildTree(new ArrayList<>(deptDtoSet)), HttpStatus.OK);
    }

    @ApiOperation("导出部门数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@dtf.check('dept:list')")
    public void download(HttpServletResponse response, DeptQueryCriteria criteria) throws IllegalAccessException, IOException {
        deptService.download(deptService.queryAll(criteria, false), response);
    }
}