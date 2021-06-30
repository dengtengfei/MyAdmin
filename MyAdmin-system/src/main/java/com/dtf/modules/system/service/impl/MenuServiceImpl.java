package com.dtf.modules.system.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.dtf.exception.EntityExistException;
import com.dtf.modules.system.domain.Menu;
import com.dtf.modules.system.domain.vo.MenuMetaVo;
import com.dtf.modules.system.domain.vo.MenuVo;
import com.dtf.modules.system.repository.MenuRepository;
import com.dtf.modules.system.repository.UserRepository;
import com.dtf.modules.system.service.MenuService;
import com.dtf.modules.system.service.RoleService;
import com.dtf.modules.system.service.dto.MenuDto;
import com.dtf.modules.system.service.dto.RoleSmallDto;
import com.dtf.modules.system.service.mapstruct.MenuMapper;
import com.dtf.utils.RedisUtils;
import com.dtf.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 0 *
 * 1 * @Author:  deng.tengfei
 * 2 * @email:  imdtf@qq.com
 * 3 * @Date:  2021/6/22 22:11
 */
@Service
@RequiredArgsConstructor
@Cacheable(cacheNames = "menu")
public class MenuServiceImpl implements MenuService {
    private final MenuRepository menuRepository;
    private final UserRepository userRepository;
    private final MenuMapper menuMapper;
    private final RoleService roleService;
    private final RedisUtils redisUtils;

    @Override
    public void create(Menu menu) {
        if (menuRepository.findByTitle(menu.getTitle()) != null) {
            throw new EntityExistException(Menu.class, "title", menu.getTitle());
        }
        // TODO 待完成
        menuRepository.save(menu);
    }

    @Override
    public List<MenuDto> findByUser(Long userId) {
        List<RoleSmallDto> roles = roleService.findByUsersId(userId);
        Set<Long> roleIds = roles.stream().map(RoleSmallDto::getId).collect(Collectors.toSet());
        LinkedHashSet<Menu> menus = menuRepository.findByRoleIdsAndTypeNot(roleIds, 2);
        return menus.stream().map(menuMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public List<MenuDto> buildTree(List<MenuDto> menuDtoList) {
        List<MenuDto> trees = new ArrayList<>();
        Set<Long> ids = new HashSet<>();
        for (MenuDto menuDto : menuDtoList) {
            if (menuDto.getPid() == null) {
                trees.add(menuDto);
            }
            for (MenuDto it : menuDtoList) {
                if (menuDto.getId().equals(it.getPid())) {
                    if (menuDto.getChildren() == null) {
                        menuDto.setChildren(new ArrayList<>());
                    }
                    menuDto.getChildren().add(it);
                    ids.add(it.getId());
                }
            }
        }
        if (trees.isEmpty()) {
            trees = menuDtoList.stream().filter(menuDto -> !ids.contains(menuDto.getId())).collect(Collectors.toList());
        }
        return trees;
    }

    @Override
    public List<MenuVo> buildMenus(List<MenuDto> menuDtoList) {
        List<MenuVo> list = new LinkedList<>();
        menuDtoList.forEach(menuDto -> {
            if (menuDto != null) {
                List<MenuDto> menuDtoChildren = menuDto.getChildren();
                MenuVo menuVo = new MenuVo();
                menuVo.setName(StringUtils.isNotBlank(menuDto.getComponentName()) ? menuDto.getComponentName() : menuDto.getTitle());
                // 一级目录需要加斜杠
                menuVo.setPath(menuDto.getPid() == null ? "/" + menuDto.getPath() : menuDto.getPath());
                menuVo.setHidden(menuDto.getHidden());
                // 如果不是外链
                if (!menuDto.getIFrame()) {
                    if (menuDto.getPid() == null) {
                        menuVo.setComponent(StringUtils.isNotBlank(menuDto.getComponent()) ? menuDto.getComponent() : "Layout");
                        // 如果不是以及菜单，且菜单类型为目录，则代表是多级菜单
                    } else if (menuDto.getType() == 0) {
                        menuVo.setComponent(StringUtils.isNotBlank(menuDto.getComponent()) ? menuDto.getComponent() : "ParentView");
                    } else if (StringUtils.isNotBlank(menuDto.getComponent())) {
                        menuVo.setComponent(menuDto.getComponent());
                    }
                }
                menuVo.setMeta(new MenuMetaVo(menuDto.getTitle(), menuDto.getIcon(), !menuDto.getCache()));
                if (CollectionUtil.isNotEmpty(menuDtoChildren)) {
                    menuVo.setAlwaysShow(true);
                    menuVo.setRedirect("noredircet");
                    menuVo.setChildren(buildMenus(menuDtoChildren));
                    // 处理是一级菜单且没有子菜单的情况
                } else if (menuDto.getPid() == null) {
                    MenuVo menuVo1 = new MenuVo();
                    menuVo1.setMeta(menuVo.getMeta());
                    // 非外链
                    if (!menuDto.getIFrame()) {
                        menuVo1.setPath("index");
                        menuVo1.setName(menuVo.getName());
                        menuVo1.setComponent(menuVo.getComponent());
                    } else {
                        menuVo1.setPath(menuDto.getPath());
                    }
                    menuVo.setName(null);
                    menuVo.setMeta(null);
                    menuVo.setComponent("Layout");
                    List<MenuVo> list1 = new ArrayList<>();
                    list1.add(menuVo1);
                    menuVo.setChildren(list1);
                }
                list.add(menuVo);
            }
        });
        return list;
    }
}
