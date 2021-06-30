package com.dtf.modules.system.repository;

import com.dtf.modules.system.domain.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 0 *
 * 1 * @Author:  deng.tengfei
 * 2 * @email:  imdtf@qq.com
 * 3 * @Date:  2021/6/22 22:16
 */
public interface MenuRepository extends JpaRepository<Menu, Long>, JpaSpecificationExecutor<Menu> {
    /**
     * 根据角色ID与菜单类型查询
     * @param roleIds \
     * @param type \
     * @return \
     */
    @Query(value = "SELECT m.* FROM sys_menu m, sys_roles_menus r WHERE m.menu_id = r.menu_id AND r.role_id IN ?1 AND m.type!=?2 ORDER BY m.menu_sort asc", nativeQuery = true)
    LinkedHashSet<Menu> findByRoleIdsAndTypeNot(Set<Long> roleIds, int type);

    /**
     * 根据标题查找
     * @param title
     * @return
     */
    Menu findByTitle(String title);
}
