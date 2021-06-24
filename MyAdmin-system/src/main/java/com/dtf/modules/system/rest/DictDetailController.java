package com.dtf.modules.system.rest;

import com.dtf.modules.system.service.DictDetailService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    private static final String ENTITY_NAME = "diceDetail";
}
