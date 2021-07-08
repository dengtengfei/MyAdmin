package com.dtf.modules.system.service.dto;

import com.dtf.annotation.Query;
import lombok.Data;

/**
 * 0 *
 * 1 * @Author:  deng.tengfei
 * 2 * @email:  imdtf@qq.com
 * 3 * @Date:  2021/7/8 23:46
 */
@Data
public class DictQueryCriteria {
    @Query(blurry = "name,description")
    private String blurry;
}
