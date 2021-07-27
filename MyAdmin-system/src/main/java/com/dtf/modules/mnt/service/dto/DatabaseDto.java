package com.dtf.modules.mnt.service.dto;

import com.dtf.base.BaseDTO;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 0 *
 * 1 * @Author:  deng.tengfei
 * 2 * @email:  imdtf@qq.com
 * 3 * @Date:  2021/7/27 22:32
 */
@Getter
@Setter
public class DatabaseDto extends BaseDTO implements Serializable {
    private String id;

    private String name;

    private String jdbcUrl;

    private String userName;

    private String pwd;
}
