package com.dtf.modules.system.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.dtf.exception.BadRequestException;
import com.dtf.exception.EntityExistException;
import com.dtf.modules.system.domain.Menu;
import com.dtf.modules.system.domain.Role;
import com.dtf.modules.system.domain.User;
import com.dtf.modules.system.domain.vo.MenuMetaVo;
import com.dtf.modules.system.domain.vo.MenuVo;
import com.dtf.modules.system.repository.MenuRepository;
import com.dtf.modules.system.repository.UserRepository;
import com.dtf.modules.system.service.MenuService;
import com.dtf.modules.system.service.RoleService;
import com.dtf.modules.system.service.dto.MenuDto;
import com.dtf.modules.system.service.dto.MenuQueryCriteria;
import com.dtf.modules.system.service.dto.RoleSmallDto;
import com.dtf.modules.system.service.mapstruct.MenuMapper;
import com.dtf.utils.*;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.Field;
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
        if (StringUtils.isNotBlank(menu.getComponentName()) && menuRepository.findByComponentName(menu.getComponentName()) != null) {
            throw new EntityExistException(Menu.class, "componentName", menu.getComponentName());
        }
        if (menu.getPid().equals(0L)) {
            menu.setPid(null);
        }
        checkUrl(menu);
        menuRepository.save(menu);
        menu.setSubCount(0);
        updateSubCnt(menu.getPid());
    }

    @Override
    public void delete(Set<Menu> menus) {
        for (Menu menu : menus) {
            delCaches(menu.getId());
            roleService.untiedMenu(menu.getId());
            menuRepository.deleteById(menu.getId());
            updateSubCnt(menu.getPid());
        }
    }

    @Override
    public void update(Menu menu) {
        if (menu.getId().equals(menu.getPid())) {
            throw new BadRequestException("上级菜单不能为自己");
        }
        Menu oldMenu = menuRepository.findById(menu.getId()).orElseGet(Menu::new);
        ValidationUtil.isNull(oldMenu.getId(), "Menu", "id", menu.getId());
        checkUrl(menu);
        Menu menuByTitle = menuRepository.findByTitle(menu.getTitle());
        if (menuByTitle != null && !menu.getId().equals(menuByTitle.getId())) {
            throw new EntityExistException(Menu.class, "title", menu.getTitle());
        }
        if (menu.getPid().equals(0L)) {
            menu.setPid(null);
        }

        if (StringUtils.isNotBlank(menu.getComponentName())) {
            Menu menuByComponentName = menuRepository.findByComponentName(menu.getComponentName());
            if (menuByComponentName != null && !menu.getId().equals(menuByComponentName.getId())) {
                throw new EntityExistException(Menu.class, "componentName", menu.getComponentName());
            }
        }
        oldMenu.setTitle(menu.getTitle());
        oldMenu.setComponent(menu.getComponent());
        oldMenu.setPath(menu.getPath());
        oldMenu.setIcon(menu.getIcon());
        oldMenu.setIFrame(menu.getIFrame());
        oldMenu.setPid(menu.getPid());
        oldMenu.setMenuSort(menu.getMenuSort());
        oldMenu.setCache(menu.getCache());
        oldMenu.setHidden(menu.getHidden());
        oldMenu.setComponentName(menu.getComponentName());
        oldMenu.setPermission(menu.getPermission());
        oldMenu.setType(menu.getType());
        menuRepository.save(oldMenu);
        updateSubCnt(oldMenu.getId());
        updateSubCnt(menu.getId());
        delCaches(menu.getId());
    }

    @Override
    public List<MenuDto> queryAll(MenuQueryCriteria criteria, boolean isQuery) throws IllegalAccessException {
        Sort sort = Sort.by(Sort.Direction.ASC, "menuSort");
        if (isQuery) {
            criteria.setPidIsNull(true);
            List<Field> fields = QueryHelp.getAllFields(criteria.getClass(), new ArrayList<>());
            for (Field field : fields) {
                field.setAccessible(true);
                Object val = field.get(criteria);
                if ("pidIsNull".equals(field.getName())) {
                    continue;
                }
                if (ObjectUtil.isNotNull(val)) {
                    criteria.setPidIsNull(null);
                    break;
                }
            }
        }
        return menuMapper.toDto(menuRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder), sort));
    }

    @Override
    public List<MenuDto> findByUser(Long userId) {
        List<RoleSmallDto> roles = roleService.findByUsersId(userId);
        Set<Long> roleIds = roles.stream().map(RoleSmallDto::getId).collect(Collectors.toSet());
        LinkedHashSet<Menu> menus = menuRepository.findByRoleIdsAndTypeNot(roleIds, 2);
        return menus.stream().map(menuMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public List<Menu> findByIdIn(Set<Long> ids) {
        return menuRepository.findByIdIn(ids);
    }

    @Override
    public Set<Menu> getChildrenMenu(Set<Menu> menuSet) {
        for (Menu menu : menuSet) {
            Set<Menu> menus = menuRepository.findByPid(menu.getId());
            if (CollectionUtil.isNotEmpty(menus)) {
                menuSet.addAll(getChildrenMenu(menus));
            }
        }
        return menuSet;
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

    private void updateSubCnt(Long id) {
        if (id != null) {
            menuRepository.updateSubCntById(menuRepository.countByPid(id), id);
        }
    }

    private void checkUrl(Menu menu) {
        if (menu.getIFrame()) {
            String http = "http://", https = "https://";
            if (!menu.getPath().toLowerCase().startsWith(http) || !menu.getPath().toLowerCase().startsWith(https)) {
                throw new BadRequestException("外链必须以http://或者https://开头");
            }
        }
    }

    private void delCaches(Long id) {
        List<User> users = userRepository.findByMenuId(id);
        redisUtils.del(CacheKey.MENU_ID + id);
        redisUtils.delByKeys(CacheKey.MENU_USER, users.stream().map(User::getId).collect(Collectors.toSet()));

        List<Role> roles = roleService.findInMenuId(new ArrayList<Long>() {{
            add(id);
        }});
        redisUtils.delByKeys(CacheKey.ROLE_ID, roles.stream().map(Role::getId).collect(Collectors.toSet()));
    }
}
