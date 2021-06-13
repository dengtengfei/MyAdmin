package com.dtf.modules.system.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.dtf.modules.system.domain.User;
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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

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
//    private final RedisUtils1 redisUtils;

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
        List<DeptDto> list = deptMapper.toDto(deptRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteriaQuery, criteriaBuilder), sort));
        // TODO 测试去掉if结果
        if (StringUtils.isBlank(dataScopeType)) {
            return deduplication(list);
        }
        return list;
    }

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

    private void updateSubCnt(Long deptid) {
        if (deptid != null) {
            int count = deptRepository.countByPid(deptid);
            deptRepository.updateSubCntById(count, deptid);
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
//        redisUtils.delByKeys(CacheKey.DATA_USER, users.stream().map(User::getId).collect(Collectors.toSet()));
//        redisUtils.del(CacheKey.DEPT_ID + id);
    }
}
