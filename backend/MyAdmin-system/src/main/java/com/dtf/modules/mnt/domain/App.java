package com.dtf.modules.mnt.domain;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.dtf.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.io.Serializable;

/**
 * 0 *
 * 1 * @Author:  deng.tengfei
 * 2 * @email:  imdtf@qq.com
 * 3 * @Date:  2021/7/14 19:35
 */
@Entity
@Getter
@Setter
@Table(name = "mnt_app")
public class App extends BaseEntity implements Serializable {
    @Id
    @Column(name = "app_id")
    @Null(groups = {Create.class}, message = "创建时id必须为空")
    @NotNull(groups = {Update.class}, message = "修改时id不能为空")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "ID", hidden = true)
    private Long id;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "端口")
    private int port;

    @ApiModelProperty(value = "上传路径")
    private String uploadPath;

    @ApiModelProperty(value = "部署路径")
    private String deployPath;

    @ApiModelProperty(value = "备份路径")
    private String backupPath;

    @ApiModelProperty(value = "启动脚本")
    private String startScript;

    @ApiModelProperty(value = "部署脚本")
    private String deployScript;

    public void copy(App app) {
        BeanUtil.copyProperties(app, this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
