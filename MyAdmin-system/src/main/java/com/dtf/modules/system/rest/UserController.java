package com.dtf.modules.system.rest;

import com.dtf.modules.system.domain.Dept;
import com.dtf.modules.system.service.*;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
}
