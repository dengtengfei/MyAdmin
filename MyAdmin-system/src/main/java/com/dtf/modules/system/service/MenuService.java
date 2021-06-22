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
    List<MenuDto> findByUser(Long userId);

    List<MenuDto> buildTree(List<MenuDto> menuDtoList);

    Object buildMenus(List<MenuDto> menuDtoList);
}
