package com.dtf.modules.system.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * 0 *
 * 1 * @Author:  deng.tengfei
 * 2 * @email:  imdtf@qq.com
 * 3 * @Date:  2021/6/22 22:58
 */
@Data
@AllArgsConstructor
public class MenuMetaVo implements Serializable {
    private String title;
    private String icon;
    private Boolean noCache;
}
