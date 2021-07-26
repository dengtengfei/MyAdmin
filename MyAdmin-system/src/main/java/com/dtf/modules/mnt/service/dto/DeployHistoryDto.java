package com.dtf.modules.mnt.service.dto;

import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 0 *
 * 1 * @Author:  deng.tengfei
 * 2 * @email:  imdtf@qq.com
 * 3 * @Date:  2021/7/26 23:02
 */
@Data
public class DeployHistoryDto implements Serializable {
    private String id;

    private String appName;

    private String ip;

    private Timestamp deployDate;

    private String deployUser;

    private Long deployId;
}
