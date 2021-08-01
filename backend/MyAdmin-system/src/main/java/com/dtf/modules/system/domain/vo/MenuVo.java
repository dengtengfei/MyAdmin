package com.dtf.modules.system.domain.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 0 *
 * 1 * @Author:  deng.tengfei
 * 2 * @email:  imdtf@qq.com
 * 3 * @Date:  2021/6/22 22:56
 */
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class MenuVo implements Serializable {
    private String name;
    private String path;
    private Boolean hidden;
    private String redirect;
    private String component;
    private Boolean alwaysShow;
    private MenuMetaVo meta;
    private List<MenuVo> children;
}
