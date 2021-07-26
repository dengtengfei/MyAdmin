package com.dtf.modules.mnt.domain;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 0 *
 * 1 * @Author:  deng.tengfei
 * 2 * @email:  imdtf@qq.com
 * 3 * @Date:  2021/7/15 21:11
 */
@Entity
@Getter
@Setter
@Table(name = "mnt_deploy_history")
public class DeployHistory implements Serializable {
    @Id
    @Column(name = "history_id")
    @ApiModelProperty(value = "ID", hidden = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @ApiModelProperty(value = "应用名称")
    private String appName;

    @ApiModelProperty(value = "IP")
    private String ip;

    @ApiModelProperty(value = "部署时间")
    private Timestamp deployDate;

    @ApiModelProperty(value = "部署者")
    private String deployUser;

    @ApiModelProperty(value = "部署ID")
    private Long deployId;

    public void copy(DeployHistory deployHistory) {
        BeanUtil.copyProperties(deployHistory, this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
