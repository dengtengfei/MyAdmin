package com.dtf.modules.system.service.dto;

import com.dtf.base.BaseDTO;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

/**
 * 0 *
 * 1 * @Author:  deng.tengfei
 * 2 * @email:  imdtf@qq.com
 * 3 * @Date:  2021/6/14 21:20
 */
@Getter
@Setter
public class RoleDto extends BaseDTO implements Serializable {
    private Long id;

    private Set<MenuDto> menuList;

    private Set<DeptDto> deptList;

    private String name;

    private String dataScope;

    private Integer level;

    private String description;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RoleDto roleDto = (RoleDto) o;
        return Objects.equals(id, roleDto.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
