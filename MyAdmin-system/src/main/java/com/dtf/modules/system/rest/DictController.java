package com.dtf.modules.system.rest;

import com.dtf.modules.system.service.DictService;
import com.dtf.modules.system.service.dto.DictQueryCriteria;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 0 *
 * 1 * @Author:  deng.tengfei
 * 2 * @email:  imdtf@qq.com
 * 3 * @Date:  2021/7/8 23:43
 */
@RestController
@Api(tags = "系统: 字典管理")
@RequiredArgsConstructor
@RequestMapping("/api/dict")
public class DictController {
    private final DictService dictService;

    @ApiOperation("查询字典")
    @GetMapping
    @PreAuthorize("@dtf.check('dict:list')")
    public ResponseEntity<Object> queryAll() {
        return new ResponseEntity<>(dictService.queryAll(new DictQueryCriteria()), HttpStatus.OK);
    }
}
