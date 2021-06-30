package com.dtf.modules.system.service;

import com.dtf.modules.system.domain.Role;
import com.dtf.modules.system.service.dto.RoleDto;
import com.dtf.modules.system.service.dto.RoleQueryCriteria;
import com.dtf.modules.system.service.dto.RoleSmallDto;
import com.dtf.modules.system.service.dto.UserDto;
import org.springframework.data.domain.Pageable;
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

    /**
     * 修改角色
     * @param role \
     */
    void update(Role role);

    /**
     * 更新角色的菜单
     * @param role \
     * @param roleDto \
     */
    void updateMenu(Role role, RoleDto roleDto);

    /**
     * 查询所有角色
     * @return \
     */
    List<RoleDto> queryAll();

    /**
     * 分页根据条件查询全部
     * @param criteria 条件
     * @param pageable 分页
     * @return \
     */
    Object queryAll(RoleQueryCriteria criteria, Pageable pageable);

    /**
     * 根据条件查询全部
     * @param criteria 条件
     * @return \
     */
    List<RoleDto> queryAll(RoleQueryCriteria criteria);

    /**
     * 根据 id 查询
     * @param id \
     * @return \
     */
    RoleDto findById(long id);

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
     * 导出角色数据
     * @param queryAll \
     * @param response \
     * @throws IOException \
     */
    void download(List<RoleDto> queryAll, HttpServletResponse response) throws IOException;

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
//    void untiedMenu(Long id);
//
//    List<Role> findInMenuId(List<Long> menuIds);
}
