package com.dtf.modules.system.rest;

import com.dtf.annotation.Log;
import com.dtf.config.RsaProperties;
import com.dtf.exception.BadRequestException;
import com.dtf.modules.system.domain.Dept;
import com.dtf.modules.system.domain.User;
import com.dtf.modules.system.domain.vo.UserPassVo;
import com.dtf.modules.system.service.*;
import com.dtf.modules.system.service.dto.RoleSmallDto;
import com.dtf.modules.system.service.dto.UserDto;
import com.dtf.modules.system.service.dto.UserQueryCriteria;
import com.dtf.utils.PageUtil;
import com.dtf.utils.RsaUtils;
import com.dtf.utils.SecurityUtils;
import com.dtf.utils.enums.CodeEnum;
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
import java.util.Set;
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

    @Log("删除用户")
    @ApiOperation("删除用户")
    @DeleteMapping
    @PreAuthorize("@dtf.check('user:del')")
    public ResponseEntity<Object> delete(@RequestBody Set<Long> ids) {
        for (Long id : ids) {
            Integer currentLevel = Collections.min(roleService.findByUsersId(SecurityUtils.getCurrentUserId()).stream().map(RoleSmallDto::getLevel).collect(Collectors.toList()));
            Integer optLevel = Collections.min(roleService.findByUsersId(id).stream().map(RoleSmallDto::getLevel).collect(Collectors.toList()));
            if (currentLevel > optLevel) {
                throw new BadRequestException("角色权限不足, 无法删除: " + userService.findById(id).getUsername());
            }
        }
        userService.delete(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Log("修改用户")
    @ApiOperation("修改用户")
    @PutMapping
    @PreAuthorize("@dtf.check('user:edit')")
    public ResponseEntity<Object> update(@Validated(User.Update.class) @RequestBody User user) throws Exception {
        checkLevel(user);
        userService.update(user);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Log("修改用户，个人中心")
    @ApiOperation("修改用户，个人中心")
    @PutMapping(value = "center")
    public ResponseEntity<Object> updateCenter(@Validated(User.Update.class) @RequestBody User user) {
        if (!user.getId().equals(SecurityUtils.getCurrentUserId())) {
            throw new BadRequestException("不能修改他人资料");
        }
        userService.updateCenter(user);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("修改密码")
    @ApiOperation("修改密码")
    @PostMapping("/updatePass")
    public ResponseEntity<Object> updatePassword(@Validated @RequestBody UserPassVo passVo) throws Exception {
        String oldPass = RsaUtils.decryptByPrivateKey(RsaProperties.privateKey, passVo.getOldPass());
        String newPass = RsaUtils.decryptByPrivateKey(RsaProperties.privateKey, passVo.getNewPass());
        UserDto userDto = userService.findByName(SecurityUtils.getCurrentUsername());
        if (!passwordEncoder.matches(oldPass, userDto.getPassword())) {
            throw new BadRequestException("修改失败，旧密码错误");
        }
        if (oldPass.equals(newPass)) {
            throw new BadRequestException("新密码不能与旧密码相同");
        }
        userService.updatePass(userDto.getUsername(), passwordEncoder.encode(newPass));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Log("修改邮箱")
    @ApiOperation("修改邮箱")
    @PostMapping(value = "/updateEmail/{code}")
    public ResponseEntity<Object> updateEmail(@PathVariable String code, @RequestBody User user) throws Exception {
        String password = RsaUtils.decryptByPrivateKey(RsaProperties.privateKey, user.getPassword());
        UserDto userDto = userService.findByName(user.getUsername());
        if (!passwordEncoder.matches(password, userDto.getPassword())) {
            throw new BadRequestException("密码错误");
        }
        verifyService.validated(CodeEnum.EMAIL_RESET_EMAIL_CODE.getKey() + user.getEmail(), code);
        userService.updateEmail(userDto.getUsername(), user.getEmail());
        return new ResponseEntity<>(HttpStatus.OK);
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
     *
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
