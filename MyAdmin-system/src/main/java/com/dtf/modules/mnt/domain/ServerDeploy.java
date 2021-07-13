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
import java.util.Objects;

/**
 * 0 *
 * 1 * @Author:  deng.tengfei
 * 2 * @email:  imdtf@qq.com
 * 3 * @Date:  2021/7/13 23:14
 */
@Entity
@Getter
@Setter
@Table(name = "mnt_server")
public class ServerDeploy extends BaseEntity implements Serializable {
    @Id
    @Column(name = "server_id")
    @Null(groups = {Create.class}, message = "创建时id必须为空")
    @NotNull(groups = {Update.class}, message = "修改时id不能为空")
    @ApiModelProperty(value = "ID", hidden = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ApiModelProperty(value = "服务器名称")
    private String name;

    @ApiModelProperty(value = "IP")
    private String ip;

    @ApiModelProperty(value = "端口")
    private Integer port;

    // TODO modify front name
    /**
     *
     */
    @Column(name = "username")
    @ApiModelProperty(value = "账号")
    private String account;

    @ApiModelProperty(value = "密码")
    private String password;

    public void copy(ServerDeploy source) {
        BeanUtil.copyProperties(source, this, CopyOptions.create().setIgnoreNullValue(true));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ServerDeploy serverDeploy = (ServerDeploy) o;
        return Objects.equals(id, serverDeploy.id) && Objects.equals(name, serverDeploy.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
