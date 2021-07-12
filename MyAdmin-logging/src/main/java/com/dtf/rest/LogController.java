package com.dtf.rest;

import com.dtf.annotation.Log;
import com.dtf.service.LogService;
import com.dtf.service.dto.LogQueryCriteria;
import com.dtf.utils.SecurityUtils;
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

/**
 * 0 *
 * 1 * @Author:  deng.tengfei
 * 2 * @email:  imdtf@qq.com
 * 3 * @Date:  2021/7/12 22:01
 */
@Api(tags = "系统: 日志管理")
@RestController
@RequestMapping("api/logs")
@RequiredArgsConstructor
public class LogController {
    private final LogService logService;

    @Log("删除所有INFO日志")
    @ApiOperation("删除所有INFO日志")
    @DeleteMapping(value = "/del/info")
    @PreAuthorize("@dtf.check()")
    public ResponseEntity<Object> deleteAllInfoLog() {
        logService.deleteAllInfoLog();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Log("删除所有ERROR日志")
    @ApiOperation("删除所有ERROR日志")
    @DeleteMapping(value = "/del/error")
    @PreAuthorize("@dtf.check()")
    public ResponseEntity<Object> deleteAllErrorLog() {
        logService.deleteAllErrorLog();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping
    @ApiOperation("日志查询")
    @PreAuthorize("@dtf.check()")
    public ResponseEntity<Object> query(LogQueryCriteria criteria, Pageable pageable) {
        criteria.setLogType("INFO");
        return new ResponseEntity<>(logService.queryAll(criteria, pageable), HttpStatus.OK);
    }

    @GetMapping("/user")
    @ApiOperation("用户日志查询")
    public ResponseEntity<Object> queryUserLog(LogQueryCriteria criteria, Pageable pageable) {
        criteria.setLogType("INFO");
        criteria.setBlurry(SecurityUtils.getCurrentUsername());
        return new ResponseEntity<>(logService.queryUserLog(criteria, pageable), HttpStatus.OK);
    }

    @GetMapping("/error")
    @ApiOperation("错误日志查询")
    @PreAuthorize("@dtf.check()")
    public ResponseEntity<Object> queryErrorLog(LogQueryCriteria criteria, Pageable pageable) {
        criteria.setLogType("ERROR");
        return new ResponseEntity<>(logService.queryUserLog(criteria, pageable), HttpStatus.OK);
    }

    @GetMapping("/error/{id}")
    @ApiOperation("日志异常详情查询")
    @PreAuthorize("@dtf.check()")
    public ResponseEntity<Object> queryErrorLogInfo(@PathVariable Long id) {
        return new ResponseEntity<>(logService.findByErrDetail(id), HttpStatus.OK);
    }

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@dtf.check()")
    public void download(HttpServletResponse response, LogQueryCriteria criteria) throws IOException {
        criteria.setLogType("INFO");
        logService.download(logService.queryAll(criteria), response);
    }

    @Log("导出错误数据")
    @ApiOperation("导出错误数据")
    @GetMapping(value = "/error/download")
    @PreAuthorize("@dtf.check()")
    public void downloadErrorLog(HttpServletResponse response, LogQueryCriteria criteria) throws IOException {
        criteria.setLogType("ERROR");
        logService.download(logService.queryAll(criteria), response);
    }
}
