package com.dtf.modules.system.rest;

import com.dtf.annotation.Log;
import com.dtf.modules.system.domain.DictDetail;
import com.dtf.modules.system.service.DictDetailService;
import com.dtf.modules.system.service.dto.DictDetailDto;
import com.dtf.modules.system.service.dto.DictDetailQueryCriteria;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 0 *
 * 1 * @Author:  deng.tengfei
 * 2 * @email:  imdtf@qq.com
 * 3 * @Date:  2021/6/24 22:03
 */
@RestController
@RequiredArgsConstructor
@Api(tags = "系统: 字典详情管理")
@RequestMapping("/api/dictDetail")
public class DictDetailController {
    private final DictDetailService dictDetailService;

    @Log("新增字典详情")
    @ApiOperation("新增字典详情")
    @PostMapping
    @PreAuthorize("@dtf.check('dict:add')")
    public ResponseEntity<Object> create(@Validated(DictDetail.Create.class) @RequestBody DictDetail dictDetail) {
        dictDetailService.create(dictDetail);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Log("删除字典详情")
    @ApiOperation("删除字典详情")
    @DeleteMapping(value = "/{id}")
    @PreAuthorize("@dtf.check('dict:del')")
    public ResponseEntity<Object> delete(@PathVariable Long id) {
        dictDetailService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Log("修改字典详情")
    @ApiOperation("修改字典详情")
    @PutMapping
    @PreAuthorize("@dtf.check('dict:eidt')")
    public ResponseEntity<Object> update(@Validated(DictDetail.Update.class) @RequestBody DictDetail dictDetail) {
        dictDetailService.update(dictDetail);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @ApiOperation("查询字典详情")
    @GetMapping
    public ResponseEntity<Object> query(DictDetailQueryCriteria criteria, @PageableDefault(sort = {"dictSort"}, direction = Sort.Direction.ASC) Pageable pageable) {
        return new ResponseEntity<>(dictDetailService.queryAll(criteria, pageable), HttpStatus.OK);
    }

    @ApiOperation("查询多个字典详情")
    @GetMapping("/map")
    public ResponseEntity<Object> getDictDetailMap(@RequestParam String dictName) {
        String[] names = dictName.split("[,，]");
        Map<String, List<DictDetailDto>> dictMap = new HashMap<>(16);
        for (String name : names) {
            dictMap.put(name, dictDetailService.getDictByName(name));
        }
        return new ResponseEntity<>(dictMap, HttpStatus.OK);
    }
}
