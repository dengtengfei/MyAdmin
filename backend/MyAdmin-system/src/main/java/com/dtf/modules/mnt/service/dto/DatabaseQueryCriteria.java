package com.dtf.modules.mnt.service.dto;

import com.dtf.annotation.Query;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

/**
 * 0 *
 * 1 * @Author:  deng.tengfei
 * 2 * @email:  imdtf@qq.com
 * 3 * @Date:  2021/7/27 22:30
 */
@Data
public class DatabaseQueryCriteria {
    @Query(type = Query.Type.INNER_LIKE)
    private String name;

    @Query
    private String jdbcUrl;

    @Query(type = Query.Type.BETWEEN)
    private List<Timestamp> createTime;
}
