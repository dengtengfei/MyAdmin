package com.dtf.modules.system.repository;

import com.dtf.modules.system.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
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
     * 根据用户id查询
     * @param id
     * @return
     */
    @Query(value = "SELECT r.* FROM sys_role r, sys_users_roles u WHERE r.role_id = u.role_id AND u.user_id = ?1", nativeQuery = true)
    Set<Role> findByUserId(Long id);
}
