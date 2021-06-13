package com.dtf.modules.system.rest;

import com.dtf.annotation.Log;
import com.dtf.exception.BadRequestException;
import com.dtf.modules.system.domain.Dept;
import com.dtf.modules.system.service.DeptService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

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
    /*
    部门
     */
    private static final String ENTITY_NAME = "dept";

    @Log("新增部门")
    @ApiOperation("新增部门")
    @PostMapping
    @PreAuthorize("el.check('dept:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody Dept dept) {
        if (dept.getId() != null) {
            throw new BadRequestException("A new " + ENTITY_NAME + "cannot created, because it has an id.");
        }
        deptService.create(dept);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @ApiOperation("导出部门数据")
    @GetMapping(value = "/download")
//    @PreAuthorize("@el.check('dept:list')")
    public void download(HttpServletResponse response) {
    }
}
