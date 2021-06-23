package com.dtf.modules.security.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 0 *
 * 1 * @Author:  deng.tengfei
 * 2 * @email:  imdtf@qq.com
 * 3 * @Date:  2021/6/14 0:26
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OnlineUserDto implements Serializable {
    // TODO Serializable ?
    /**
     *
     */
    private String username;

    private String nickName;

    private String dept;

    private String browser;

    private String ip;

    private String address;

    private String key;

    private Date loginTime;
}
