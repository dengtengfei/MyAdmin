package com.dtf.modules.system.repository;

import com.dtf.modules.system.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * 0 *
 * 1 * @Author:  deng.tengfei
 * 2 * @email:  imdtf@qq.com
 * 3 * @Date:  2021/6/13 1:33
 */
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    /**
     * 根据 id 列表删除
     * @param ids \
     */
    void deleteAllByIdIn(Set<Long> ids);

    /**
     * 修改密码
     * @param username \
     * @param password \
     * @param date \
     */
    @Modifying
    @Query(value = "UPDATE sys_user SET password = ?2, pwd_reset_time = ?3 WHERE username = ?1", nativeQuery = true)
    void updatePass(String username, String password, Date date);

    /**
     * 修改邮箱
     * @param username \
     * @param email \
     */
    @Modifying
    @Query(value = "UPDATE sys_user SET email = ?2 WHERE username = ?1", nativeQuery = true)
    void updateEmail(String username, String email);

    /**
     * 根据用户名查询
     * @param username 用户名
     * @return \
     */
    User findByUsername(String username);

    /**
     * 根据邮箱查询
     * @param email \
     * @return \
     */
    User findByEmail(String email);

    /**
     * 根据手机号码查询
     * @param phone \
     * @return \
     */
    User findByPhone(String phone);

    /**
     * 根据角色ID查询
     * @param roleId \
     * @return \
     */
    @Query(value = "SELECT u.* from sys_user u, sys_users_roles r WHERE r.role_id = ?1 AND u.user_id = r.user_id", nativeQuery = true)
    List<User> findByRoleId(Long roleId);

    /**
     * 根据角色中的部门查询
     * @param deptId \
     * @return \
     */
    @Query(value = "SELECT u.* FROM sys_user u, sys_users_roles r, sys_roles_depts d WHERE " +
    "u.user_id = r.user_id AND r.role_id = d.role_id AND d.dept_id = ?1 group by u.user_id", nativeQuery = true)
    List<User> findByRoleDeptId(Long deptId);


    /**
     * 根据菜单id查询用户列表
     * @param menuId \
     * @return \
     */
    @Query(value = "SELECT u.* FROM sys_user u, sys_users_roles ur, sys_roles_menus rm WHERE u.user_id = ur.user_id AND ur.role_id = rm.role_id AND rm.menu_id = ?1", nativeQuery = true)
    List<User> findByMenuId(Long menuId);

    // TODO 待验证

    /**
     * 根据部门id查询用户数量
     * @param deptIds \
     * @return \
     */
    int countByDeptIdIn(Set<Long> deptIds);

    /**
     * 根据角色 id 列表查询用户数量
     * @param roleIds \
     * @return \
     */
    @Query(value = "SELECT COUNT(1) FROM sys_user u, sys_users_roles r where r.role_id in ?1 AND r.user_id = u.user_id", nativeQuery = true)
    int countByRoleIds(Set<Long> roleIds);
}
