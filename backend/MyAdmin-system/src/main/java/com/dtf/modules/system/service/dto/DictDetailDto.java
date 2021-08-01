package com.dtf.modules.system.service.dto;

import com.dtf.base.BaseDTO;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 0 *
 * 1 * @Author:  deng.tengfei
 * 2 * @email:  imdtf@qq.com
 * 3 * @Date:  2021/6/24 22:19
 */
@Getter
@Setter
public class DictDetailDto extends BaseDTO implements Serializable {
    private Long id;
    private DictSmallDto dict;
    private String label;
    private String value;
    private Integer dictSort;
}
