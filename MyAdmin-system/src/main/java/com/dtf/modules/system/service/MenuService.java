package com.dtf.modules.system.service;

import com.dtf.modules.system.domain.Menu;
import com.dtf.modules.system.service.dto.MenuDto;
import com.dtf.modules.system.service.dto.MenuQueryCriteria;

import java.util.List;
import java.util.Set;

/**
 * 0 *
 * 1 * @Author:  deng.tengfei
 * 2 * @email:  imdtf@qq.com
 * 3 * @Date:  2021/6/22 22:02
 */
public interface MenuService {
    /**
     * 创建菜单
     *
     * @param menu \
     */
    void create(Menu menu);

    /**
     * 删除菜单
     *
     * @param menus \
     */
    void delete(Set<Menu> menus);

    /**
     * 修改菜单
     *
     * @param menu \
     */
    void update(Menu menu);

    /**
     * 查询全部
     *
     * @param criteria \
     * @param isQuery \
     * @return \
     * @throws IllegalAccessException \
     */
    List<MenuDto> queryAll(MenuQueryCriteria criteria, boolean isQuery) throws IllegalAccessException;

    /**
     * 根据用户id查询菜单列表
     *
     * @param userId \
     * @return \
     */
    List<MenuDto> findByUser(Long userId);

    /**
     * 根据 id 列表查询
     *
     * @param ids \
     * @return \
     */
    List<Menu> findByIdIn(Set<Long> ids);

    /**
     * 获取菜单的子菜单
     *
     * @param menus \
     * @return \
     */
    Set<Menu> getChildrenMenu(Set<Menu> menus);

    /**
     * 构建菜单树
     *
     * @param menuDtoList \
     * @return \
     */
    List<MenuDto> buildTree(List<MenuDto> menuDtoList);

    /**
     * 构建菜单
     *
     * @param menuDtoList \
     * @return \
     */
    Object buildMenus(List<MenuDto> menuDtoList);
}
