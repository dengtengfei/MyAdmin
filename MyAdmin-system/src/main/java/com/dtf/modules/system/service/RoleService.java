package com.dtf.modules.system.service;

import com.dtf.modules.system.domain.Role;
import com.dtf.modules.system.service.dto.RoleDto;
import com.dtf.modules.system.service.dto.RoleQueryCriteria;
import com.dtf.modules.system.service.dto.RoleSmallDto;
import com.dtf.modules.system.service.dto.UserDto;
import org.springframework.security.core.GrantedAuthority;

import javax.servlet.http.HttpServletResponse;
import java.awt.print.Pageable;
import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * 0 *
 * 1 * @Author:  deng.tengfei
 * 2 * @email:  imdtf@qq.com
 * 3 * @Date:  2021/6/14 21:14
 */
public interface RoleService {
//    List<RoleDto> queryAll();
//
//    RoleDto findById(long id);
//
//    void create(Role role);
//
//    void update(Role role);
//
//    void delete(Set<Long> ids);

    /**
     * 根据用户id查询
     * @param id
     * @return
     */
    List<RoleSmallDto> findByUsersId(Long id);
//
//    Integer findByRoles(Set<Role> roles);
//
//    void updateMenu(Role role, RoleDto roleDto);
//
//    void untiedMenu(Long id);
//
//    Object queryAll(RoleQueryCriteria criteria, Pageable pageable);
//
//    List<RoleDto> queryAll(RoleQueryCriteria criteria);
//
//    void download(List<RoleDto> queryAll, HttpServletResponse response) throws IOException;

    /**
     * 获取用户权限信息
     * @param user 用户信息
     * @return 权限信息
     */
    List<GrantedAuthority> mapToGrantedAuthorities(UserDto user);

//    void verification(Set<Long> ids);
//
//    List<Role> findInMenuId(List<Long> menuIds);
}
