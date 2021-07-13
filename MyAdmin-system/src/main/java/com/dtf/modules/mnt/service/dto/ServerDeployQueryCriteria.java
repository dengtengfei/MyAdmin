package com.dtf.modules.mnt.service.dto;

import com.dtf.annotation.Query;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

/**
 * 0 *
 * 1 * @Author:  deng.tengfei
 * 2 * @email:  imdtf@qq.com
 * 3 * @Date:  2021/7/13 23:39
 */
@Data
public class ServerDeployQueryCriteria {
    @Query(blurry = "name,ip,username")
    private String blurry;

    @Query(type = Query.Type.BETWEEN)
    private List<Timestamp> createTime;
}
