package com.dtf.base;

import com.dtf.utils.ToStringUtils;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 0 *
 * 1 * @Author:  deng.tengfei
 * 2 * @email:  imdtf@qq.com
 * 3 * @Date:  2021/6/12 0:06
 */
@Getter
@Setter
public class BaseDTO implements Serializable {
    private String createBy;
    private String updateBy;
    private Timestamp createTime;
    private Timestamp updateTime;

    @Override
    public String toString() {
        return ToStringUtils.toMyString(this);
    }
}
