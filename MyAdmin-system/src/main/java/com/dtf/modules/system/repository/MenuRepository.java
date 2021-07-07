package com.dtf.modules.system.repository;

import com.dtf.modules.system.domain.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * 0 *
 * 1 * @Author:  deng.tengfei
 * 2 * @email:  imdtf@qq.com
 * 3 * @Date:  2021/6/22 22:16
 */
public interface MenuRepository extends JpaRepository<Menu, Long>, JpaSpecificationExecutor<Menu> {
    /**
     * 根据id更新子节点数量
     *
     * @param count  \
     * @param menuId \
     */
    @Query(value = "UPDATE sys_menu SET sub_count = ?1 WHERE menu_id = ?2", nativeQuery = true)
    void updateSubCntById(int count, Long menuId);

    /**
     * 根据父节点查询子节点个数
     *
     * @param pid \
     * @return \
     */
    int countByPid(Long pid);

    /**
     * 根据角色ID与菜单类型查询
     *
     * @param roleIds \
     * @param type    \
     * @return \
     */
    @Query(value = "SELECT m.* FROM sys_menu m, sys_roles_menus r WHERE m.menu_id = r.menu_id AND r.role_id IN ?1 AND m.type!=?2 ORDER BY m.menu_sort asc", nativeQuery = true)
    LinkedHashSet<Menu> findByRoleIdsAndTypeNot(Set<Long> roleIds, int type);

    /**
     * 根据标题查找
     *
     * @param title \
     * @return \
     */
    Menu findByTitle(String title);

    /**
     * 根据组件名称查询
     *
     * @param componentName \
     * @return \
     */
    Menu findByComponentName(String componentName);

    /**
     * 根据 id 列表查询
     *
     * @param ids \
     * @return \
     */
    List<Menu> findByIdIn(Set<Long> ids);

    /**
     * 根据父节点 id 查询子节点列表
     * @param pid \
     * @return \
     */
    List<Menu> findByPid(Long pid);

    /**
     * 查询一级菜单
     * @return \
     */
    List<Menu> findByPidIsNull();
}
