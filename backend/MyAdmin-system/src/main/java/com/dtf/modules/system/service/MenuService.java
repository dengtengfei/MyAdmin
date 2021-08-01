package com.dtf.modules.system.service;

import com.dtf.modules.system.domain.Menu;
import com.dtf.modules.system.service.dto.MenuDto;
import com.dtf.modules.system.service.dto.MenuQueryCriteria;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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
     * @param isQuery  \
     * @return \
     * @throws IllegalAccessException \
     */
    List<MenuDto> queryAll(MenuQueryCriteria criteria, boolean isQuery) throws IllegalAccessException;

    /**
     * 根据 id 查找
     *
     * @param id \
     * @return \
     */
    Menu findOne(long id);

    /**
     * 根据 id 查找
     *
     * @param id \
     * @return \
     */
    MenuDto findById(long id);

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
     * 懒加载菜单数据
     *
     * @param pid \
     * @return \
     */
    List<MenuDto> getMenus(Long pid);

    /**
     * 获取同级与上级数据
     *
     * @param menuDto \
     * @param menuList \
     * @return \
     */
    List<MenuDto> getSuperior(MenuDto menuDto, List<Menu> menuList);

    /**
     * 获取菜单的子菜单
     *
     * @param menus   \
     * @param menuSet \
     * @return \
     */
    Set<Menu> getChildrenMenu(List<Menu> menus, Set<Menu> menuSet);

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

    /**
     * 导出菜单数据
     *
     * @param menuDtoList \
     * @param response    \
     * @throws IOException \
     */
    void download(List<MenuDto> menuDtoList, HttpServletResponse response) throws IOException;
}
