package com.dtf.service.dto;

import lombok.Data;

import java.sql.Timestamp;

/**
 * 0 *
 * 1 * @Author:  deng.tengfei
 * 2 * @email:  imdtf@qq.com
 * 3 * @Date:  2021/7/12 22:38
 */
@Data
public class LogSmallDTO {
    private String description;

    private String requestIP;

    private Long time;

    private String address;

    private String browser;

    private Timestamp createTime;
}
