package com.dtf.modules.system.domain.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 0 *
 * 1 * @Author:  deng.tengfei
 * 2 * @email:  imdtf@qq.com
 * 3 * @Date:  2021/6/27 12:29
 */
@Data
public class UserPassVo {
    @NotNull
    private String oldPass;
    @NotNull
    private String newPass;
}
