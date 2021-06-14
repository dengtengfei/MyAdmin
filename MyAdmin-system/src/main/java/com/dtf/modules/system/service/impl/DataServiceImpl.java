package com.dtf.modules.system.service.impl;

import com.dtf.modules.system.domain.Dept;
import com.dtf.modules.system.service.DataService;
import com.dtf.modules.system.service.DeptService;
import com.dtf.modules.system.service.RoleService;
import com.dtf.modules.system.service.dto.RoleSmallDto;
import com.dtf.modules.system.service.dto.UserDto;
import com.dtf.utils.enums.DataScopeEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 0 *
 * 1 * @Author:  deng.tengfei
 * 2 * @email:  imdtf@qq.com
 * 3 * @Date:  2021/6/14 22:10
 */
@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "data")
public class DataServiceImpl implements DataService {
    private final RoleService roleService;
    private final DeptService deptService;

    @Override
    @Cacheable(key = "'user:' + #p0.id")
    public List<Long> getDeptIds(UserDto user) {
        // 存储部门id
        Set<Long> deptIds = new HashSet<>();
        // 查询用户角色
        List<RoleSmallDto> roleSmallDtoList = roleService.findByUsersId(user.getId());
        // 获取对应部门id
        for (RoleSmallDto role : roleSmallDtoList) {
            DataScopeEnum dataScopeEnum = DataScopeEnum.find(role.getDataScope());
            switch (Objects.requireNonNull(dataScopeEnum)) {
                case THIS_LEVEL:
                    break;
                case CUSTOMIZE:
                    deptIds.addAll(getCustomize(deptIds, role));
                    break;
                default:
                    return new ArrayList<>(deptIds);
            }
        }
        return new ArrayList<>(deptIds);
    }

    public Set<Long> getCustomize(Set<Long> deptIds, RoleSmallDto role) {
        Set<Dept> deptList = deptService.findByRoleId(role.getId());
        for (Dept dept : deptList) {
            deptIds.add(dept.getId());
            List<Dept> deptChildren = deptService.findByPid(dept.getPid());
            if (deptChildren != null && deptChildren.size() != 0) {
                deptIds.addAll(deptService.getDeptChildren(deptChildren));
            }
        }
        return deptIds;
    }
}
