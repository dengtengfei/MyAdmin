package com.dtf.modules.system.rest;

import com.dtf.modules.system.service.MenuService;
import com.dtf.modules.system.service.dto.MenuDto;
import com.dtf.modules.system.service.mapstruct.MenuMapper;
import com.dtf.utils.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    @GetMapping("/build")
    @ApiOperation("获取前端所需菜单")
    public ResponseEntity<Object> buildMenus() {
        List<MenuDto> menuDtoList = menuService.buildTree(menuService.findByUser(SecurityUtils.getCurrentUserId()));
        return new ResponseEntity<>(menuService.buildMenus(menuDtoList), HttpStatus.OK);
    }
}
