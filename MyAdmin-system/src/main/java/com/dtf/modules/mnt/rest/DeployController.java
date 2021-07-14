package com.dtf.modules.mnt.rest;

import com.dtf.annotation.Log;
import com.dtf.modules.mnt.domain.Deploy;
import com.dtf.modules.mnt.service.DeployService;
import com.dtf.modules.mnt.service.dto.DeployQueryCriteria;
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
 * 3 * @Date:  2021/7/14 20:42
 */
@Api("运维: 部署管理")
@RestController
@RequestMapping("api/deploy")
@RequiredArgsConstructor
public class DeployController {
    private final DeployService deployService;

    @Log("新增应用部署")
    @ApiOperation("新增应用部署")
    @PostMapping
    @PreAuthorize("@dtf.check('deploy:add')")
    public ResponseEntity<Object> create(@Validated(Deploy.Create.class) @RequestBody Deploy deploy) {
        deployService.create(deploy);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Log("删除应用部署")
    @ApiOperation("删除应用部署")
    @DeleteMapping
    @PreAuthorize("@dtf.check('deploy:del')")
    public ResponseEntity<Object> delete(@RequestBody Set<Long> ids) {
        deployService.delete(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Log("修改应用部署")
    @ApiOperation("修改应用部署")
    @PutMapping
    @PreAuthorize("@dtf.check('deploy:edit')")
    public ResponseEntity<Object> update(@Validated(Deploy.Update.class) @RequestBody Deploy deploy) {
        deployService.update(deploy);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @ApiOperation("查询应用部署")
    @GetMapping
    @PreAuthorize("@dtf.check('deploy:list')")
    public ResponseEntity<Object> query(DeployQueryCriteria criteria, Pageable pageable) {
        return new ResponseEntity<>(deployService.queryAll(criteria, pageable), HttpStatus.OK);
    }

    @ApiOperation("查询应用部署")
    @GetMapping(value = "/download")
    @PreAuthorize("@dtf.check('deploy:list')")
    public void download(HttpServletResponse response, DeployQueryCriteria criteria) throws IOException {
        deployService.download(deployService.queryAll(criteria), response);
    }
}
