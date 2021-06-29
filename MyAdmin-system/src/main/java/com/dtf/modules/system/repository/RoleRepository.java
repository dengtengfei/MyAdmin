package com.dtf.modules.system.repository;

import com.dtf.modules.system.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

/**
 * 0 *
 * 1 * @Author:  deng.tengfei
 * 2 * @email:  imdtf@qq.com
 * 3 * @Date:  2021/6/14 21:40
 */
public interface RoleRepository extends JpaRepository<Role, Long>, JpaSpecificationExecutor<Role> {
    /**
     * 根据 id 删除
     * @param ids
     */
    void deleteAllByIdIn(Set<Long> ids);

    /**
     * 根据用户id查询
     *
     * @param id \
     * @return \
     */
    @Query(value = "SELECT r.* FROM sys_role r, sys_users_roles u WHERE r.role_id = u.role_id AND u.user_id = ?1", nativeQuery = true)
    Set<Role> findByUserId(Long id);

    /**
     * 根据角色名称查询
     *
     * @param name \
     * @return \
     */
    Role findByName(String name);

    /**
     * 根据部门id查询用户数量
     *
     * @param deptIds \
     * @return \
     */
    @Query(value = "select count(1) from sys_role r, sys_roles_depts d where r.role_id = d.role_id and d.dept_id in ?1", nativeQuery = true)
    int countByDeptIds(Set<Long> deptIds);
}
