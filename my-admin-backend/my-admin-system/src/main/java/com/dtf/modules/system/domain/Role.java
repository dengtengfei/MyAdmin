package com.dtf.modules.system.domain;

import com.alibaba.fastjson.annotation.JSONField;
import com.dtf.base.BaseEntity;
import com.dtf.utils.enums.DataScopeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

/**
 * 0 *
 * 1 * @Author:  deng.tengfei
 * 2 * @email:  imdtf@qq.com
 * 3 * @Date:  2021/6/11 0:06
 */
@Getter
@Setter
@Entity
@Table(name = "sys_role")
public class Role extends BaseEntity implements Serializable {
    @Id
    @Column(name = "role_id")
    @Null(groups = {Create.class}, message = "创建时id必须为空")
    @NotNull(groups = {Update.class}, message = "修改时id不能为空")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "ID", hidden = true)
    private Long id;

    @JSONField(serialize = false)
    @ManyToMany(mappedBy = "roles")
    @ApiModelProperty(value = "用户", hidden = true)
    private Set<User> users;

    @ManyToMany
    @JoinTable(name = "sys_roles_menus",
            joinColumns = {@JoinColumn(name = "role_id", referencedColumnName = "role_id")},
            inverseJoinColumns = {@JoinColumn(name = "menu_id", referencedColumnName = "menu_id")})
    @ApiModelProperty(value = "菜单", hidden = true)
    private Set<Menu> menus;

    @ManyToMany
    @JoinTable(name = "sys_roles_depts",
            joinColumns = {@JoinColumn(name = "role_id", referencedColumnName = "role_id")},
            inverseJoinColumns = {@JoinColumn(name = "dept_id", referencedColumnName = "dept_id")})
    @ApiModelProperty(value = "部门", hidden = true)
    private Set<Dept> depts;

    @NotBlank
    @ApiModelProperty(value = "名称", hidden = true)
    private String name;

    @ApiModelProperty(value = "数据权限：全部、本级、自定义")
    private String dataScope = DataScopeEnum.THIS_LEVEL.getValue();

    @Column(name = "level")
    @ApiModelProperty(value = "级别，数值越小，级别越大")
    private Integer level = 3;

    @ApiModelProperty(value = "描述")
    private String description;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        return Objects.equals(this.id, ((Role)o).id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
