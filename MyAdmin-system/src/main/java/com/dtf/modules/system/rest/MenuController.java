package com.dtf.modules.system.rest;

import cn.hutool.core.collection.CollectionUtil;
import com.dtf.annotation.Log;
import com.dtf.modules.system.domain.Menu;
import com.dtf.modules.system.service.MenuService;
import com.dtf.modules.system.service.dto.MenuDto;
import com.dtf.modules.system.service.dto.MenuQueryCriteria;
import com.dtf.modules.system.service.mapstruct.MenuMapper;
import com.dtf.utils.PageUtil;
import com.dtf.utils.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 0 *
 * 1 * @Author:  deng.tengfei
 * 2 * @email:  imdtf@qq.com
 * 3 * @Date:  2021/6/22 21:58
 */
@RestController
@RequiredArgsConstructor
@Api(tags = "系统: 菜单管理")
@RequestMapping("/api/menus")
public class MenuController {
    private final MenuService menuService;
    private final MenuMapper menuMapper;

    @Log("新增菜单")
    @ApiOperation("新增菜单")
    @PostMapping
    @PreAuthorize("@dtf.check('menu:add')")
    public ResponseEntity<Object> create(@Validated(Menu.Create.class) @RequestBody Menu menu) {
        menuService.create(menu);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Log("删除菜单")
    @ApiOperation("删除菜单")
    @DeleteMapping
    @PreAuthorize("@dtf.check('menu:del')")
    public ResponseEntity<Object> delete(@RequestBody Set<Long> ids) {
        // TODO 校验别人的权限？
        Set<Menu> menus = new HashSet<>(menuService.findByIdIn(ids));
        for (Long id : ids) {
            List<MenuDto> menuDtoList = menuService.getMenus(id);
            menus.add(menuService.findOne(id));
            menus = menuService.getChildrenMenu(menuMapper.toEntity(menuDtoList), menus);
        }
        menuService.delete(menus);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Log("修改菜单")
    @ApiOperation("修改菜单")
    @PutMapping
    @PreAuthorize("@dtf.check('menu:edit')")
    public ResponseEntity<Object> update(@Validated(Menu.Update.class) @RequestBody Menu menu) {
        menuService.update(menu);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping
    @ApiOperation("查询菜单")
    @PreAuthorize("@dtf.check('menu:list')")
    public ResponseEntity<Object> query(MenuQueryCriteria criteria) throws IllegalAccessException {
        List<MenuDto> menuDtoList = menuService.queryAll(criteria, true);
        return new ResponseEntity<>(PageUtil.toPage(menuDtoList, menuDtoList.size()), HttpStatus.OK);
    }

    @GetMapping(value = "/lazy")
    @ApiOperation("返回全部菜单")
    @PreAuthorize("@dtf.check('menu:list', 'role:list')")
    public ResponseEntity<Object> query(@RequestParam Long pid) {
        return new ResponseEntity<>(menuService.getMenus(pid), HttpStatus.OK);
    }

    @ApiOperation("查询同级与上级菜单")
    @PostMapping(value = "/superior")
    @PreAuthorize("@dtf.check('menu:list')")
    public ResponseEntity<Object> getSuperior(@RequestBody List<Long> ids) {
        Set<MenuDto> menuDtoSet = new LinkedHashSet<>();
        if (CollectionUtil.isNotEmpty(ids)) {
            for (Long id : ids) {
                MenuDto menuDto = menuService.findById(id);
                menuDtoSet.addAll(menuService.getSuperior(menuDto, new ArrayList<>()));
            }
            return new ResponseEntity<>(menuService.buildTree(new ArrayList<>(menuDtoSet)), HttpStatus.OK);
        }
        return new ResponseEntity<>(menuService.getMenus(null), HttpStatus.OK);
    }

    @ApiOperation("返回子菜单ID, 包含自身")
    @GetMapping(value = "/child")
    @PreAuthorize("@dtf.check('menu:list', 'role:list')")
    public ResponseEntity<Object> getChild(@RequestParam Long id) {
        Set<Menu> menuSet = new HashSet<>();
        List<MenuDto> menus = new ArrayList<>(menuService.getMenus(id));
        menuSet.add(menuService.findOne(id));
        menuSet = menuService.getChildrenMenu(menuMapper.toEntity(menus), menuSet);
        return new ResponseEntity<>(menuSet.stream().map(Menu::getId).collect(Collectors.toSet()), HttpStatus.OK);
    }

    @ApiOperation("导出菜单")
    @GetMapping(value = "download")
    @PreAuthorize("@dtf.check('menu:list')")
    public void download(HttpServletResponse response, MenuQueryCriteria criteria) throws Exception {
        menuService.download(menuService.queryAll(criteria, false), response);
    }

    @GetMapping("/build")
    @ApiOperation("获取前端所需菜单")
    public ResponseEntity<Object> buildMenus() {
        List<MenuDto> menuDtoList = menuService.buildTree(menuService.findByUser(SecurityUtils.getCurrentUserId()));
        return new ResponseEntity<>(menuService.buildMenus(menuDtoList), HttpStatus.OK);
    }
}
