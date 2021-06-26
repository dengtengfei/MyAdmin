package com.dtf.modules.system.rest;

import com.dtf.annotation.Log;
import com.dtf.exception.BadRequestException;
import com.dtf.modules.system.domain.Dept;
import com.dtf.modules.system.domain.User;
import com.dtf.modules.system.service.*;
import com.dtf.modules.system.service.dto.RoleSmallDto;
import com.dtf.modules.system.service.dto.UserQueryCriteria;
import com.dtf.utils.PageUtil;
import com.dtf.utils.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 0 *
 * 1 * @Author:  deng.tengfei
 * 2 * @email:  imdtf@qq.com
 * 3 * @Date:  2021/6/25 23:55
 */
@Api("系统: 用户管理")
@RestController
@RequestMapping("api/users")
@RequiredArgsConstructor
public class UserController {
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final DataService dataService;
    private final DeptService deptService;
    private final RoleService roleService;
    private final VerifyService verifyService;

    @Log("新增用户")
    @ApiOperation("新增用户")
    @PostMapping
    @PreAuthorize("@dtf.check('user:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody User user) {
        checkLevel(user);
        user.setPassword(passwordEncoder.encode("123456"));
        userService.create(user);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @ApiOperation("查询用户")
    @GetMapping
    @PreAuthorize("@dtf.check('user:list')")
    public ResponseEntity<Object> query(UserQueryCriteria criteria, Pageable pageable) {
        if (!ObjectUtils.isEmpty(criteria.getDeptId())) {
            criteria.getDeptIds().add(criteria.getDeptId());
            List<Dept> deptChildren = deptService.findByPid(criteria.getDeptId());
            criteria.getDeptIds().addAll(deptService.getDeptChildren(deptChildren));
        }
        List<Long> datScopes = dataService.getDeptIds(userService.findByName(SecurityUtils.getCurrentUsername()));
        if (!CollectionUtils.isEmpty(criteria.getDeptIds()) && CollectionUtils.isEmpty(datScopes)) {
            criteria.getDeptIds().retainAll(datScopes);
            if (!CollectionUtils.isEmpty(criteria.getDeptIds())) {
                return new ResponseEntity<>(userService.queryAll(criteria, pageable), HttpStatus.OK);
            }
        } else {
            criteria.getDeptIds().addAll(datScopes);
            return new ResponseEntity<>(userService.queryAll(criteria, pageable), HttpStatus.OK);
        }
        return new ResponseEntity<>(PageUtil.toPage(null, 0), HttpStatus.OK);
    }

    /**
     * level 值越小 权限越大
     * @param user \
     */
    private void checkLevel(User user) {
        Integer currentLevel = Collections.min(roleService.findByUsersId(SecurityUtils.getCurrentUserId()).stream().map(RoleSmallDto::getLevel).collect(Collectors.toList()));
        Integer optLevel = roleService.findByRoles(user.getRoles());
        if (currentLevel > optLevel) {
            throw new BadRequestException("角色权限不足");
        }
    }
}
