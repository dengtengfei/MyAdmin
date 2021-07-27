package com.dtf.modules.mnt.rest;

import com.dtf.annotation.Log;
import com.dtf.modules.mnt.domain.Database;
import com.dtf.modules.mnt.service.DatabaseService;
import com.dtf.modules.mnt.service.dto.DatabaseQueryCriteria;
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
 * 3 * @Date:  2021/7/27 23:15
 */
@RestController
@RequiredArgsConstructor
@Api(tags = "运维: 数据库管理")
@RequestMapping("/api/database")
public class DatabaseController {
    private final DatabaseService databaseService;

    @Log("新增数据库")
    @ApiOperation("新增数据库")
    @PostMapping
    @PreAuthorize("@dtf.check('database:add')")
    public ResponseEntity<Object> create(@Validated(Database.Create.class) @RequestBody Database database) {
        databaseService.create(database);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Log("删除数据库")
    @ApiOperation("删除数据库")
    @DeleteMapping
    @PreAuthorize("@dtf.check('database:del')")
    public ResponseEntity<Object> create(@RequestBody Set<String> ids) {
        databaseService.delete(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Log("修改数据库")
    @ApiOperation("修改数据库")
    @PutMapping
    @PreAuthorize("@dtf.check('database:edit')")
    public ResponseEntity<Object> update(@Validated(Database.Update.class) @RequestBody Database database) {
        databaseService.update(database);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @ApiOperation("查询数据库")
    @GetMapping
    @PreAuthorize("@dtf.check('database:list')")
    public ResponseEntity<Object> query(DatabaseQueryCriteria criteria, Pageable pageable) {
        return new ResponseEntity<>(databaseService.queryAll(criteria, pageable), HttpStatus.CREATED);
    }

    @ApiOperation("导出数据库数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@dtf.check('database:list')")
    public void download(HttpServletResponse response, DatabaseQueryCriteria criteria) throws IOException {
        databaseService.download(databaseService.queryAll(criteria), response);
    }

    @Log("测试数据库连接")
    @ApiOperation("测试数据库连接")
    @PostMapping(value = "/testConnection")
    @PreAuthorize("@dtf.check('database:testConnection')")
    public ResponseEntity<Object> testConnection(@Validated(Database.Create.class) @RequestBody Database database) {
        return new ResponseEntity<>(databaseService.testConnection(database), HttpStatus.CREATED);
    }

    // TODO 执行sql脚本
}
