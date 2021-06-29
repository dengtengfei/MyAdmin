package com.dtf.modules.system.rest;

import cn.hutool.core.lang.Dict;
import com.dtf.annotation.Log;
import com.dtf.exception.BadRequestException;
import com.dtf.modules.system.domain.Role;
import com.dtf.modules.system.service.RoleService;
import com.dtf.modules.system.service.dto.RoleDto;
import com.dtf.modules.system.service.dto.RoleSmallDto;
import com.dtf.utils.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 0 *
 * 1 * @Author:  deng.tengfei
 * 2 * @email:  imdtf@qq.com
 * 3 * @Date:  2021/6/27 0:20
 */
@RestController
@Api(tags = "系统: 角色管理")
@RequiredArgsConstructor
@RequestMapping("/api/roles")
public class RoleController {
    private final RoleService roleService;
    private static final String ENTITY_NAME = "role";

    @Log("新增角色")
    @ApiOperation("新增角色")
    @PostMapping
    @PreAuthorize("@dtf.check('roles:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody Role role) {
        if (role.getId() != null) {
            throw new BadRequestException("A new " + ENTITY_NAME + " cannot create, because it has an ID.");
        }
        getLevels(role.getLevel());
        roleService.create(role);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Log("删除角色")
    @ApiOperation("删除角色")
    @DeleteMapping
    @PreAuthorize("@dtf.check('role:del')")
    public ResponseEntity<Object> delete(@RequestBody Set<Long> ids) {
        for (Long id : ids) {
            RoleDto roleDto = roleService.findById(id);
            getLevels(roleDto.getLevel());
        }

        roleService.verification(ids);
        roleService.delete(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation("查询全部角色")
    @GetMapping("/all")
    @PreAuthorize("@dtf.check('roles:list','user:add','user:edit')")
    public ResponseEntity<Object> query() {
        return new ResponseEntity<>(roleService.queryAll(), HttpStatus.OK);
    }

    @ApiOperation("获取用户级别")
    @GetMapping("/level")
    public ResponseEntity<Object> getLevel() {
        return new ResponseEntity<>(Dict.create().set("level", getLevels(null)), HttpStatus.OK);
    }

    private int getLevels(Integer level) {
        List<Integer> levels = roleService.findByUsersId(SecurityUtils.getCurrentUserId()).stream().map(RoleSmallDto::getLevel).collect(Collectors.toList());
        int min = Collections.min(levels);
        if (level != null && level < min) {
            throw new BadRequestException("权限不足，你的角色级别" + min + ", 低于操作的角色级别" + level);
        }
        return min;
    }
}
