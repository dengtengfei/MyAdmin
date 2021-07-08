package com.dtf.modules.system.service.dto;

import com.dtf.base.BaseDTO;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * 0 *
 * 1 * @Author:  deng.tengfei
 * 2 * @email:  imdtf@qq.com
 * 3 * @Date:  2021/7/8 23:47
 */
@Getter
@Setter
public class DictDto extends BaseDTO implements Serializable {
    private Long id;

    private List<DictDetailDto> dictDetails;

    private String name;

    private String description;
}
