package com.dtf.modules.system.rest;

import com.dtf.annotation.Log;
import com.dtf.modules.system.domain.Dict;
import com.dtf.modules.system.service.DictService;
import com.dtf.modules.system.service.dto.DictQueryCriteria;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

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

    @Log("新增字典")
    @ApiOperation("新增字典")
    @PostMapping
    @PreAuthorize("@dtf.check('dict:add')")
    public ResponseEntity<Object> create(@Validated(Dict.Create.class) @RequestBody Dict dict) {
        dictService.create(dict);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Log("删除字典")
    @ApiOperation("删除字典")
    @DeleteMapping
    @PreAuthorize("@dtf.check('dict:del')")
    public ResponseEntity<Object> delete(@RequestBody Set<Long> ids) {
        dictService.delete(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Log("修改字典")
    @ApiOperation("修改字典")
    @PutMapping
    @PreAuthorize("@dtf.check('dict:edit')")
    public ResponseEntity<Object> update(@Validated(Dict.Update.class) @RequestBody Dict dict) {
        dictService.update(dict);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @ApiOperation("查询字典")
    @GetMapping
    @PreAuthorize("@dtf.check('dict:list')")
    public ResponseEntity<Object> query(DictQueryCriteria criteria, Pageable pageable) {
        return new ResponseEntity<>(dictService.queryAll(criteria, pageable), HttpStatus.OK);
    }

    @ApiOperation("查询字典")
    @GetMapping(value = "/all")
    @PreAuthorize("@dtf.check('dict:list')")
    public ResponseEntity<Object> queryAll() {
        return new ResponseEntity<>(dictService.queryAll(new DictQueryCriteria()), HttpStatus.OK);
    }

    @ApiOperation("导出字典数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@dtf.check('dict:list')")
    public void download(HttpServletResponse response, DictQueryCriteria criteria) throws IOException {
        dictService.download(dictService.queryAll(criteria), response);
    }
}
