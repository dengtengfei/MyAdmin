package com.dtf.modules.mnt.rest;

import com.dtf.annotation.Log;
import com.dtf.modules.mnt.service.DeployHistoryService;
import com.dtf.modules.mnt.service.dto.DeployHistoryQueryCriteria;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

/**
 * 0 *
 * 1 * @Author:  deng.tengfei
 * 2 * @email:  imdtf@qq.com
 * 3 * @Date:  2021/7/26 22:52
 */
@Api(tags = "运维: 部署历史管理")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/deployHistory")
public class DeployHistoryController {
    private final DeployHistoryService deployHistoryService;

    @ApiOperation("查询部署历史")
    @GetMapping
    @PreAuthorize("@dtf.check('deployHistory:list')")
    public ResponseEntity<Object> query(DeployHistoryQueryCriteria criteria, Pageable pageable) {
        return new ResponseEntity<>(deployHistoryService.queryAll(criteria, pageable), HttpStatus.OK);
    }

    @ApiOperation("导出部署历史数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@dtf.check('deployHistory:list')")
    public void download(HttpServletResponse response, DeployHistoryQueryCriteria criteria) throws IOException {
        deployHistoryService.download(deployHistoryService.queryAll(criteria), response);
    }

    @Log("删除DeployHistory")
    @ApiOperation(value = "删除DeployHistory")
    @DeleteMapping
    @PreAuthorize("@dtf.check('deployHistory:del')")
    public ResponseEntity<Object> delete(@RequestBody Set<String> ids) {
        deployHistoryService.delete(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
