package com.dtf.modules.system.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.dtf.exception.BadRequestException;
import com.dtf.modules.system.domain.User;
import com.dtf.modules.system.repository.RoleRepository;
import com.dtf.modules.system.service.dto.DeptDto;
import com.dtf.modules.system.service.mapstruct.DeptMapper;
import com.dtf.modules.system.domain.Dept;
import com.dtf.modules.system.repository.DeptRepository;
import com.dtf.modules.system.repository.UserRepository;
import com.dtf.modules.system.service.DeptService;
import com.dtf.modules.system.service.dto.DeptQueryCriteria;
import com.dtf.utils.*;
import com.dtf.utils.enums.DataScopeEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 0 *
 * 1 * @Author:  deng.tengfei
 * 2 * @email:  imdtf@qq.com
 * 3 * @Date:  2021/6/12 0:34
 */
@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "dept")
public class DeptServiceImpl implements DeptService {
    private final DeptRepository deptRepository;
    private final DeptMapper deptMapper;
    private final UserRepository userRepository;
    private final RedisUtils redisUtils;
    private final RoleRepository roleRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(Dept dept) {
        deptRepository.save(dept);
        // 计算子节点数目
        dept.setSubCount(0);
        // 清理缓存
        updateSubCnt(dept.getPid());
        // 清理自定义角色权限的dataScope
        delCaches(dept.getPid());
    }

    @Override
    public void delete(Set<DeptDto> deptDtoSet) {
        for (DeptDto deptDto : deptDtoSet) {
            delCaches(deptDto.getId());
            deptRepository.deleteById(deptDto.getId());
            updateSubCnt(deptDto.getPid());
        }
    }

    @Override
    public void update(Dept dept) {
        Dept oldDept = deptRepository.findById(dept.getId()).orElseGet(Dept::new);
        Long oldPid = oldDept.getPid();
        Long newPid = dept.getPid();
        if (dept.getId() != null && dept.getId().equals(dept.getPid())) {
            throw new BadRequestException("上级不能为自己");
        }
        ValidationUtil.isNull(dept.getId(), "Dept", "id", oldDept.getId());
        // TODO 没有 set id
        updateSubCnt(oldPid);
        updateSubCnt(newPid);
        delCaches(dept.getId());
    }

    @Override
    public List<DeptDto> queryAll(DeptQueryCriteria criteria, Boolean isQuery) throws IllegalAccessException {
        Sort sort = Sort.by(Sort.Direction.ASC, "deptSort");
        String dataScopeType = SecurityUtils.getDataScopeType();
        if (isQuery) {
            if (dataScopeType.equals(DataScopeEnum.ALL.getValue())) {
                criteria.setPidIsNull(true);
            }
            List<Field> fields = QueryHelp.getAllFields(criteria.getClass(), new ArrayList<>());
            List<String> fieldNames = new ArrayList<String>() {{
                add("pidIsNull");
                add("enabled");
            }};
            for (Field field : fields) {
                // 设置对象的访问权限，保证对private的属性访问
                field.setAccessible(true);
                Object val = field.get(criteria);
                if (fieldNames.contains(field.getName())) {
                    continue;
                }
                if (ObjectUtil.isNotNull(val)) {
                    criteria.setPidIsNull(null);
                    break;
                }
            }
        }
        List<DeptDto> list = deptMapper.toDto(deptRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder), sort));
        // TODO 测试去掉if结果
        if (StringUtils.isBlank(dataScopeType)) {
            return deduplication(list);
        }
        return list;
    }

    @Override
    public DeptDto findById(Long id) {
        Dept dept = deptRepository.findById(id).orElseGet(Dept::new);
        ValidationUtil.isNull(dept.getId(), "Dept", "id", id);
        return deptMapper.toDto(dept);
    }

    private void updateSubCnt(Long deptId) {
        if (deptId != null) {
            int count = deptRepository.countByPid(deptId);
            deptRepository.updateSubCntById(count, deptId);
        }
    }

    private List<DeptDto> deduplication(List<DeptDto> list) {
        List<DeptDto> deptDtoList = new ArrayList<>();
        for (DeptDto deptDto : list) {
            boolean flag = true;
            for (DeptDto dto : list) {
                if (dto.getId().equals(deptDto.getPid())) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                deptDtoList.add(deptDto);
            }
        }
        return deptDtoList;
    }

    private void delCaches(Long id) {
        List<User> users = userRepository.findByRoleDeptId(id);
        // 删除数据权限
        redisUtils.delByKeys(CacheKey.DATA_USER, users.stream().map(User::getId).collect(Collectors.toSet()));
        redisUtils.del(CacheKey.DEPT_ID + id);
    }

    @Override
    public Set<Dept> findByRoleId(Long id) {
        return deptRepository.findByRoleId(id);
    }

    @Override
    public List<Dept> findByPid(long pid) {
        return deptRepository.findByPid(pid);
    }

    @Override
    public List<Long> getDeptChildren(List<Dept> deptList) {
        List<Long> list = new ArrayList<>();
        deptList.forEach(dept -> {
            if (dept != null && dept.getEnabled()) {
                List<Dept> deptChildren = deptRepository.findByPid(dept.getPid());
                if (deptChildren.size() != 0) {
                    list.addAll(getDeptChildren(deptChildren));
                }
                list.add(dept.getId());
            }
        });
        return list;
    }

    @Override
    public Set<DeptDto> getDeleteDeptLst(List<Dept> deptList, Set<DeptDto> deptDtoSet) {
        for (Dept dept : deptList) {
            deptDtoSet.add(deptMapper.toDto(dept));
            List<Dept> deptChildren = deptRepository.findByPid(dept.getId());
            if (CollectionUtil.isNotEmpty(deptChildren)) {
                getDeleteDeptLst(deptChildren, deptDtoSet);
            }
        }
        return deptDtoSet;
    }

    @Override
    public List<DeptDto> getSuperior(DeptDto deptDto, List<Dept> deptList) {
        if (deptDto.getPid() == null) {
            deptList.addAll(deptRepository.findByPidIsNull());
            return deptMapper.toDto(deptList);
        }
        deptList.addAll(deptRepository.findByPid(deptDto.getPid()));
        // parent
        return getSuperior(findById(deptDto.getPid()), deptList);
    }

    @Override
    public Object buildTree(List<DeptDto> deptDtoList) {
        // TODO
        Set<DeptDto> treeSet = new LinkedHashSet<>(), deptSet = new LinkedHashSet<>();
        List<String> deptNames = deptDtoList.stream().map(DeptDto::getName).collect(Collectors.toList());
        boolean isChild;
        for (DeptDto deptDto : deptDtoList) {
            isChild = false;
            if (deptDto.getPid() == null) {
                treeSet.add(deptDto);
            }
            for (DeptDto it : deptDtoList) {
                if (it.getPid() != null && it.getPid().equals(deptDto.getId())) {
                    isChild = true;
                    if (deptDto.getChildren() == null) {
                        deptDto.setChildren(new ArrayList<>());
                    }
                    deptDto.getChildren().add(it);
                }
            }
            if (isChild) {
                deptSet.add(deptDto);
            } else if (deptDto.getPid() != null && !deptNames.contains(findById(deptDto.getPid()).getName())) {
                deptSet.add(deptDto);
            }
        }

        if (CollectionUtil.isEmpty(treeSet)) {
            treeSet = deptSet;
        }
        Map<String, Object> map = new HashMap<>(2);
        map.put("totalElements", deptDtoList.size());
        map.put("content", CollectionUtil.isEmpty(treeSet) ? deptDtoList : treeSet);
        return map;
    }

    @Override
    public void verification(Set<DeptDto> deptDtoSet) {
        Set<Long> deptIds = deptDtoSet.stream().map(DeptDto::getId).collect(Collectors.toSet());
        if (userRepository.countByDeptIdIn(deptIds) > 0) {
            throw new BadRequestException("所选部门存在用户关联，请解除后再试！");
        }
        if (roleRepository.countByDeptIds(deptIds) > 0) {
            throw new BadRequestException("所选部门存在角色关联，请解除后再试！");
        }
    }

    @Override
    public void download(List<DeptDto> deptDtoList, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (DeptDto deptDto : deptDtoList) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("部门名称", deptDto.getName());
            map.put("部门状态", deptDto.getEnabled() ? "启用" : "停用");
            map.put("创建日期", deptDto.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}
