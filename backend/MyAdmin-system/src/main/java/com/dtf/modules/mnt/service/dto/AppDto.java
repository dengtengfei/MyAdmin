package com.dtf.modules.mnt.service.dto;

import com.dtf.base.BaseDTO;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 0 *
 * 1 * @Author:  deng.tengfei
 * 2 * @email:  imdtf@qq.com
 * 3 * @Date:  2021/7/14 19:35
 */
@Getter
@Setter
public class AppDto extends BaseDTO implements Serializable {
    private Long id;

    private String name;

    private int port;

    private String uploadPath;

    private String deployPath;

    private String backupPath;

    private String startScript;

    private String deployScript;
}
