package com.dtf.modules.system.service.dto;

import com.dtf.annotation.Query;
import lombok.Data;

/**
 * 0 *
 * 1 * @Author:  deng.tengfei
 * 2 * @email:  imdtf@qq.com
 * 3 * @Date:  2021/6/24 22:17
 */
@Data
public class DictDetailQueryCriteria {
    @Query(type = Query.Type.INNER_LIKE)
    private String label;

    @Query(propName = "name", joinName = "dict")
    private String name;
}
