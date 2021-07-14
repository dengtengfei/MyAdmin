package com.dtf.modules.mnt.rest;

import com.dtf.annotation.Log;
import com.dtf.modules.mnt.domain.App;
import com.dtf.modules.mnt.service.AppService;
import com.dtf.modules.mnt.service.dto.AppQueryCriteria;
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
 * 3 * @Date:  2021/7/14 19:33
 */
@Api(tags = "运维: 应用管理")
@RestController
@RequestMapping("/api/app")
@RequiredArgsConstructor
public class AppController {
    private final AppService appService;

    @Log("新增应用")
    @ApiOperation(value = "新增应用")
    @PostMapping
    @PreAuthorize("@dtf.check('app:add')")
    public ResponseEntity<Object> create(@Validated(App.Create.class) @RequestBody App app) {
        appService.create(app);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Log("删除应用")
    @ApiOperation(value = "删除应用")
    @DeleteMapping
    @PreAuthorize("@dtf.check('app:del')")
    public ResponseEntity<Object> delete(Set<Long> ids) {
        appService.delete(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Log("修改应用")
    @ApiOperation(value = "修改应用")
    @PutMapping
    @PreAuthorize("@dtf.check('app:add')")
    public ResponseEntity<Object> update(@Validated(App.Update.class) @RequestBody App app) {
        appService.update(app);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @ApiOperation(value = "查询应用")
    @GetMapping
    @PreAuthorize("@dtf.check('app:list')")
    public ResponseEntity<Object> query(AppQueryCriteria criteria, Pageable pageable) {
        return new ResponseEntity<>(appService.queryAll(criteria, pageable), HttpStatus.OK);
    }

    @ApiOperation(value = "导出应用数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@dtf.check('app:list')")
    public void download(HttpServletResponse response, AppQueryCriteria criteria) throws IOException {
        appService.download(appService.queryAll(criteria), response);
    }
}
