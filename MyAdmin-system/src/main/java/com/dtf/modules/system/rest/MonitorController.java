package com.dtf.modules.system.rest;

import com.dtf.modules.system.service.MonitorService;
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
 * 3 * @Date:  2021/7/13 22:14
 */
@Api(tags = "系统: 服务监控管理")
@RestController
@RequestMapping("/api/monitor")
@RequiredArgsConstructor
public class MonitorController {
    private final MonitorService monitorService;

    @GetMapping
    @ApiOperation("查询服务监控")
    @PreAuthorize("@dtf.check('monitor:list')")
    public ResponseEntity<Object> query() {
        return new ResponseEntity<>(monitorService.getServers(), HttpStatus.OK);
    }
}
