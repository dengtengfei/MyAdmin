package com.dtf.modules.mnt.domain;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.dtf.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.io.Serializable;

/**
 * 0 *
 * 1 * @Author:  deng.tengfei
 * 2 * @email:  imdtf@qq.com
 * 3 * @Date:  2021/7/27 22:24
 */
@Entity
@Table(name = "mnt_database")
@Getter
@Setter
public class Database extends BaseEntity implements Serializable {
    @Id
    @Column(name = "db_id")
    @GeneratedValue(generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = "uuid")
    @ApiModelProperty(value = "ID", hidden = true)
    @Null(groups = {Create.class}, message = "创建时id必须为空")
    @NotNull(groups = {Update.class}, message = "修改时id不能为空")
    private String id;

    @ApiModelProperty(value = "数据库名称")
    private String name;

    @ApiModelProperty(value = "数据库连接地址")
    private String jdbcUrl;

    @ApiModelProperty(value = "用户名")
    private String userName;

    @ApiModelProperty(value = "密码")
    private String pwd;

    public void copy(Database dataBase) {
        BeanUtil.copyProperties(dataBase, this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
