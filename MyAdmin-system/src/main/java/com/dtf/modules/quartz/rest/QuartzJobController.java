package com.dtf.modules.quartz.rest;

import com.dtf.annotation.Log;
import com.dtf.modules.quartz.domain.QuartzJob;
import com.dtf.modules.quartz.service.QuartzJobService;
import com.dtf.modules.quartz.service.dto.JobsQueryCriteria;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
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
 * 3 * @Date:  2021/7/10 20:44
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/jobs")
@Api(tags = "系统: 定时任务管理")
public class QuartzJobController {
    private final QuartzJobService quartzJobService;

    @Log("创建定时任务")
    @ApiModelProperty("创建定时任务")
    @PostMapping
    @PreAuthorize("@dtf.check('timing:add')")
    public ResponseEntity<Object> create(@Validated(QuartzJob.Create.class) @RequestBody QuartzJob quartzJob) {
        quartzJobService.create(quartzJob);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Log("删除定时任务")
    @ApiModelProperty("删除定时任务")
    @DeleteMapping
    @PreAuthorize("@dtf.check('timing:del')")
    public ResponseEntity<Object> delete(@RequestBody Set<Long> ids) {
        quartzJobService.delete(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Log("修改定时任务")
    @ApiModelProperty("修改定时任务")
    @PutMapping
    @PreAuthorize("@dtf.check('timing:edit')")
    public ResponseEntity<Object> update(@Validated(QuartzJob.Update.class) @RequestBody QuartzJob quartzJob) {
        quartzJobService.update(quartzJob);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Log("更改定时任务状态")
    @ApiModelProperty("更改定时任务状态")
    @PutMapping(value = "/{id}")
    @PreAuthorize("@dtf.check('timing:del')")
    public ResponseEntity<Object> update(@PathVariable Long id) {
        quartzJobService.updateIsPause(quartzJobService.findById(id));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation("查询定时任务")
    @GetMapping
    @PreAuthorize("@dtf.check('timing:list')")
    public ResponseEntity<Object> query(JobsQueryCriteria criteria, Pageable pageable) {
        return new ResponseEntity<>(quartzJobService.queryAll(criteria, pageable), HttpStatus.OK);
    }

    @ApiOperation("查询任务执行日志")
    @GetMapping(value = "/logs")
    @PreAuthorize("@dtf.check('timing:list')")
    public ResponseEntity<Object> queryLog(JobsQueryCriteria criteria, Pageable pageable) {
        return new ResponseEntity<>(quartzJobService.queryAllLog(criteria, pageable), HttpStatus.OK);
    }

    @ApiModelProperty("导出定时任务数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@dtf.check('timing:list')")
    public void download(HttpServletResponse response, JobsQueryCriteria criteria) throws IOException {
        quartzJobService.download(quartzJobService.queryAll(criteria), response);
    }

    @ApiModelProperty("导出任务日志数据")
    @GetMapping(value = "/logs/download")
    @PreAuthorize("@dtf.check('timing:list')")
    public void downloadLog(HttpServletResponse response, JobsQueryCriteria criteria) throws IOException {
        quartzJobService.downloadLog(quartzJobService.queryAllLog(criteria), response);
    }

    @Log("执行定时任务")
    @ApiModelProperty("执行定时任务")
    @PutMapping(value = "/exec/{id}")
    @PreAuthorize("@dtf.check('timing:edit')")
    public ResponseEntity<Object> execute(@PathVariable Long id) {
        quartzJobService.execute(quartzJobService.findById(id));
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
