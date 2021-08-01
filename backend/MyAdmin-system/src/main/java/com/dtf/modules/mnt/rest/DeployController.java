package com.dtf.modules.mnt.rest;

import com.dtf.annotation.Log;
import com.dtf.modules.mnt.domain.Deploy;
import com.dtf.modules.mnt.domain.DeployHistory;
import com.dtf.modules.mnt.service.DeployService;
import com.dtf.modules.mnt.service.dto.DeployQueryCriteria;
import com.dtf.utils.FileUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
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

    @ApiOperation("导出部署数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@dtf.check('deploy:list')")
    public void download(HttpServletResponse response, DeployQueryCriteria criteria) throws IOException {
        deployService.download(deployService.queryAll(criteria), response);
    }

    @Log("上传部署文件")
    @ApiOperation("上传部署文件")
    @PostMapping(value = "/upload")
    @PreAuthorize("@dtf.check('deploy:edit')")
    public ResponseEntity<Object> upload(@RequestBody MultipartFile file, HttpServletRequest request) throws IOException {
        Long id = Long.valueOf(request.getParameter("id"));
        String fileName = "";
        if (file != null) {
            String fileSavePath = FileUtil.getTmpDirPath() + "/";
            fileName = file.getOriginalFilename();
            File deployedFile = new File(fileSavePath + fileName);
            FileUtil.del(deployedFile);
            file.transferTo(deployedFile);
            deployService.deploy(fileSavePath + fileName, id);
        } else {
            System.out.println("can not find file");
        }
        System.out.println("the previous upload file name is " + Objects.requireNonNull(file).getOriginalFilename());
        Map<String, Object> map = new HashMap<>(2);
        map.put("errno", 0);
        map.put("id", fileName);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @Log("系统还原")
    @ApiOperation("系统还原")
    @PostMapping(value = "serverReduction")
    @PreAuthorize("@dtf.check('deploy:edit')")
    public ResponseEntity<Object> serverReduction(@Validated @RequestBody DeployHistory deployHistory) {
        return new ResponseEntity<>(deployService.serverReduction(deployHistory), HttpStatus.OK);
    }

    @Log("启动服务")
    @ApiOperation("启动服务")
    @PostMapping(value = "/startServer")
    @PreAuthorize("@dtf.check('deploy:edit')")
    public ResponseEntity<Object> startServer(@Validated @RequestBody Deploy deploy) {
        return new ResponseEntity<>(deployService.startServer(deploy), HttpStatus.OK);
    }

    @Log("停止服务器")
    @ApiOperation("停止服务器")
    @PostMapping(value = "/stopServer")
    @PreAuthorize("@dtf.check('deploy:edit')")
    public ResponseEntity<Object> stopServer(@Validated @RequestBody Deploy deploy) {
        return new ResponseEntity<>(deployService.stopServer(deploy), HttpStatus.OK);
    }

    @Log("查询服务运行状态")
    @ApiOperation("查询服务运行状态")
    @PostMapping(value = "/serverStatsu")
    @PreAuthorize("@dtf.check('deploy:edit')")
    public ResponseEntity<Object> serverStatus(@Validated @RequestBody Deploy deploy) {
        return new ResponseEntity<>(deployService.serverStatus(deploy), HttpStatus.OK);
    }
}
