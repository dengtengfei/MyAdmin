package com.dtf.modules.system.service;

import com.dtf.modules.system.domain.Role;
import com.dtf.modules.system.service.dto.RoleDto;
import com.dtf.modules.system.service.dto.RoleQueryCriteria;
import com.dtf.modules.system.service.dto.RoleSmallDto;
import com.dtf.modules.system.service.dto.UserDto;
import org.springframework.security.core.GrantedAuthority;

import javax.servlet.http.HttpServletResponse;
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
    /**
     * 创建角色 \
     * @param role \
     */
    void create(Role role);

    /**
     * 删除角色
     * @param ids 要删除的角色id
     */
    void delete(Set<Long> ids);
//
//    void update(Role role);
//
//    void updateMenu(Role role, RoleDto roleDto);

    /**
     * 根据用户id查询
     * @param id \
     * @return \
     */
    List<RoleSmallDto> findByUsersId(Long id);

    /**
     * 根据角色查询角色级别
     * @param roles \
     * @return \
     */
    Integer findByRoles(Set<Role> roles);

    /**
     * 查询所有角色
     * @return \
     */
    List<RoleDto> queryAll();

    /**
     * 根据 id 查询
     * @param id \
     * @return \
     */
    RoleDto findById(long id);
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

    /**
     * 验证是否被用户关联
     * @param ids 角色id列表
     */
    void verification(Set<Long> ids);
//
//    List<Role> findInMenuId(List<Long> menuIds);
}
