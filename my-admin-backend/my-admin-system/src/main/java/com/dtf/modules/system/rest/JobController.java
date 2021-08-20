package com.dtf.modules.system.rest;

import com.dtf.annotation.Log;
import com.dtf.modules.system.domain.Job;
import com.dtf.modules.system.service.JobService;
import com.dtf.modules.system.service.dto.JobQueryCriteria;
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
 * 3 * @Date:  2021/6/27 0:35
 */
@RestController
@RequiredArgsConstructor
@Api(tags = "系统: 岗位管理")
@RequestMapping("/api/job")
public class JobController {
    private final JobService jobService;

    @Log("新增岗位")
    @ApiOperation("新增岗位")
    @PostMapping
    @PreAuthorize("@dtf.check('job:add')")
    public ResponseEntity<Object> create(@Validated(Job.Create.class) @RequestBody Job job) {
        jobService.create(job);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Log("删除岗位")
    @ApiOperation("删除岗位")
    @DeleteMapping
    @PreAuthorize("@dtf.check('job:del')")
    public ResponseEntity<Object> delete(@RequestBody Set<Long> ids) {
        jobService.verification(ids);
        jobService.delete(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Log("修改岗位")
    @ApiOperation("修改岗位")
    @PutMapping
    @PreAuthorize("@dtf.check('job:edit')")
    public ResponseEntity<Object> update(@Validated(Job.Update.class) @RequestBody Job job) {
        jobService.update(job);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping
    @ApiOperation("查询岗位")
    public ResponseEntity<Object> query(JobQueryCriteria criteria, Pageable pageable) {
        return new ResponseEntity<>(jobService.queryAll(criteria, pageable), HttpStatus.OK);
    }

    @ApiOperation("导出岗位数据")
    @GetMapping(value = "download")
    @PreAuthorize("@dtf.check('job:list')")
    public void download(HttpServletResponse response, JobQueryCriteria criteria) throws IOException {
        jobService.download(jobService.queryAll(criteria), response);
    }
}
