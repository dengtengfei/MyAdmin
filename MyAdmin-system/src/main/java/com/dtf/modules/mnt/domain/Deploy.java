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
import java.util.Set;

/**
 * 0 *
 * 1 * @Author:  deng.tengfei
 * 2 * @email:  imdtf@qq.com
 * 3 * @Date:  2021/7/14 20:43
 */
@Entity
@Getter
@Setter
@Table(name = "mnt_deploy")
public class Deploy extends BaseEntity implements Serializable {
    @Id
    @Column(name = "deploy_id")
    @Null(groups = {Create.class}, message = "创建时id必须为空")
    @NotNull(groups = {Update.class}, message = "修改时id不能为空")
    @ApiModelProperty(value = "ID", hidden = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany
    @ApiModelProperty(value = "服务器")
    @JoinTable(name = "mnt_deploy_server",
            joinColumns = {@JoinColumn(name = "deploy_id", referencedColumnName = "deploy_id")},
            inverseJoinColumns = {@JoinColumn(name = "server_id", referencedColumnName = "server_id")})
    private Set<ServerDeploy> deploys;

    @ManyToOne
    @JoinColumn(name = "app_id")
    @ApiModelProperty(value = "应用编号")
    private App app;

    public void copy(Deploy deploy) {
        BeanUtil.copyProperties(deploy, this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
