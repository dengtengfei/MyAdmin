package com.dtf.modules.system.service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 0 *
 * 1 * @Author:  deng.tengfei
 * 2 * @email:  imdtf@qq.com
 * 3 * @Date:  2021/6/13 22:12
 */
@Data
@NoArgsConstructor
public class JobSmallDto implements Serializable {
    private Long id;

    private String name;
}
