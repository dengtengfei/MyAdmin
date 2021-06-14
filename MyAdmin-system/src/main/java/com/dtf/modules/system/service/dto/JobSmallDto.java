package com.dtf.modules.system.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * 0 *
 * 1 * @Author:  deng.tengfei
 * 2 * @email:  imdtf@qq.com
 * 3 * @Date:  2021/6/13 22:12
 */
@Data
@AllArgsConstructor
public class JobSmallDto implements Serializable {
    private Long id;

    private String name;
}
