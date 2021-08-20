package com.dtf.modules.mnt.rest;

import com.dtf.annotation.Log;
import com.dtf.modules.mnt.domain.ServerDeploy;
import com.dtf.modules.mnt.service.ServerDeployService;
import com.dtf.modules.mnt.service.dto.ServerDeployQueryCriteria;
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
 * 3 * @Date:  2021/7/13 23:09
 */
@Api(tags = "运维: 服务器管理")
@RestController
@RequestMapping("/api/serverDeploy")
@RequiredArgsConstructor
public class ServerDeployController {
    private final ServerDeployService serverDeployService;

    @Log("新增服务器")
    @ApiOperation("新增服务器")
    @PostMapping
    @PreAuthorize("@dtf.check('serverDeploy:add')")
    public ResponseEntity<Object> create(@Validated(ServerDeploy.Create.class) @RequestBody ServerDeploy serverDeploy) {
        serverDeployService.create(serverDeploy);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Log("删除服务器")
    @ApiOperation("删除服务器")
    @DeleteMapping
    @PreAuthorize("@dtf.check('serverDeploy:del')")
    public ResponseEntity<Object> delete(@RequestBody Set<Long> ids) {
        serverDeployService.delete(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Log("修改服务器")
    @ApiOperation("修改服务器")
    @PutMapping
    @PreAuthorize("@dtf.check('serverDeploy:edit')")
    public ResponseEntity<Object> update(@Validated(ServerDeploy.Update.class) @RequestBody ServerDeploy serverDeploy) {
        serverDeployService.update(serverDeploy);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @ApiOperation("查询服务器")
    @GetMapping
    @PreAuthorize("@dtf.check('serverDeploy:list')")
    public ResponseEntity<Object> query(ServerDeployQueryCriteria criteria, Pageable pageable) {
        return new ResponseEntity<>(serverDeployService.queryAll(criteria, pageable), HttpStatus.OK);
    }

    @ApiOperation("导出服务器数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@dtf.check('serverDeploy:list')")
    public void download(HttpServletResponse response, ServerDeployQueryCriteria criteria) throws IOException {
        serverDeployService.download(serverDeployService.queryAll(criteria), response);
    }

    @Log("测试服务器连接")
    @ApiOperation(value = "测试服务器连接")
    @PostMapping("/testConnect")
    @PreAuthorize("@dtf.check('serverDeploy:add')")
    public ResponseEntity<Object> testConnect(@RequestBody ServerDeploy serverDeploy) {
        return new ResponseEntity<>(serverDeployService.testConnect(serverDeploy), HttpStatus.OK);
    }
}
