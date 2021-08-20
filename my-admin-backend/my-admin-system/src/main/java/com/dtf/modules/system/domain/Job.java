package com.dtf.modules.system.domain;

import com.dtf.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.io.Serializable;
import java.util.Objects;

/**
 * 0 *
 * 1 * @Author:  deng.tengfei
 * 2 * @email:  imdtf@qq.com
 * 3 * @Date:  2021/6/11 22:39
 */
@Entity
@Getter
@Setter
@Table(name = "sys_job")
public class Job extends BaseEntity implements Serializable {
    @Id
    @Column(name = "job_id")
    @Null(groups = {Create.class}, message = "创建时id必须为空")
    @NotNull(groups = {Update.class}, message = "修改时id不能为空")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "ID", hidden = true)
    private Long id;

    @NotBlank
    @ApiModelProperty(value = "岗位名称")
    private String name;

    @NotNull
    @ApiModelProperty(value = "岗位排序")
    private Long jobSort;

    @NotNull
    @ApiModelProperty(value = "是否启用")
    private Boolean enabled;

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Job job = (Job) o;
        return Objects.equals(id, job.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
