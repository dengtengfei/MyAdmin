package com.dtf.modules.system.rest;

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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private static final String ENTITY_NAME = "menu";

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
        menus.addAll(menuService.getChildrenMenu(menus));
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

    @GetMapping("/build")
    @ApiOperation("获取前端所需菜单")
    public ResponseEntity<Object> buildMenus() {
        List<MenuDto> menuDtoList = menuService.buildTree(menuService.findByUser(SecurityUtils.getCurrentUserId()));
        return new ResponseEntity<>(menuService.buildMenus(menuDtoList), HttpStatus.OK);
    }
}
