package com.dtf.modules.system.service;

import com.dtf.modules.system.service.dto.MenuDto;

import java.util.List;

/**
 * 0 *
 * 1 * @Author:  deng.tengfei
 * 2 * @email:  imdtf@qq.com
 * 3 * @Date:  2021/6/22 22:02
 */
public interface MenuService {
    /**
     * 根据用户id查询菜单列表
     * @param userId \
     * @return \
     */
    List<MenuDto> findByUser(Long userId);

    /**
     * 构建菜单树
     * @param menuDtoList \
     * @return \
     */
    List<MenuDto> buildTree(List<MenuDto> menuDtoList);

    /**
     * 构建菜单
     * @param menuDtoList \
     * @return \
     */
    Object buildMenus(List<MenuDto> menuDtoList);
}
