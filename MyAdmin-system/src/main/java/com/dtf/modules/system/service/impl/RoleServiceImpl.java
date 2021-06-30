package com.dtf.modules.system.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.dtf.exception.BadRequestException;
import com.dtf.exception.EntityExistException;
import com.dtf.modules.security.service.UserCacheClean;
import com.dtf.modules.system.domain.Menu;
import com.dtf.modules.system.domain.Role;
import com.dtf.modules.system.domain.User;
import com.dtf.modules.system.repository.RoleRepository;
import com.dtf.modules.system.repository.UserRepository;
import com.dtf.modules.system.service.RoleService;
import com.dtf.modules.system.service.dto.RoleDto;
import com.dtf.modules.system.service.dto.RoleQueryCriteria;
import com.dtf.modules.system.service.dto.RoleSmallDto;
import com.dtf.modules.system.service.dto.UserDto;
import com.dtf.modules.system.service.mapstruct.RoleMapper;
import com.dtf.modules.system.service.mapstruct.RoleSmallMapper;
import com.dtf.utils.*;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 0 *
 * 1 * @Author:  deng.tengfei
 * 2 * @email:  imdtf@qq.com
 * 3 * @Date:  2021/6/14 21:35
 */
@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "role")
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;
    private final RoleSmallMapper roleSmallMapper;
    private final UserRepository userRepository;
    private final RedisUtils redisUtils;
    private final UserCacheClean userCacheClean;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(Role role) {
        if (roleRepository.findByName(role.getName()) != null) {
            throw new EntityExistException(Role.class, "name", role.getName());
        }
        roleRepository.save(role);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Set<Long> ids) {
        for (Long id : ids) {
            delCaches(id, null);
        }
        roleRepository.deleteAllByIdIn(ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Role role) {
        Role oldRole = roleRepository.findById(role.getId()).orElseGet(Role::new);
        ValidationUtil.isNull(oldRole.getId(), "Role", "id", role.getId());
        Role role1 = roleRepository.findByName(role.getName());
        if (role1 != null && !role1.getId().equals(role.getId())) {
            throw new EntityExistException(Role.class, "name", role.getName());
        }
        oldRole.setName(role.getName());
        oldRole.setDescription(role.getDescription());
        oldRole.setDataScope(role.getDataScope());
        oldRole.setDepts(role.getDepts());
        oldRole.setLevel(role.getLevel());
        roleRepository.save(oldRole);
        delCaches(role.getId(), null);
    }

    @Override
    public void updateMenu(Role resources, RoleDto roleDto) {
        Role role = roleMapper.toEntity(roleDto);
        List<User> users = userRepository.findByRoleId(role.getId());
        role.setMenus(resources.getMenus());
        roleRepository.save(role);
        delCaches(resources.getId(), users);
    }

    @Override
    public List<RoleDto> queryAll() {
        Sort sort = Sort.by(Sort.Direction.ASC, "level");
        return roleMapper.toDto(roleRepository.findAll(sort));
    }

    @Override
    public Object queryAll(RoleQueryCriteria criteria, Pageable pageable) {
        Page<Role> roles = roleRepository.findAll(((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder)), pageable);
        return PageUtil.toPage(roles.map(roleMapper::toDto));
    }

    @Override
    public List<RoleDto> queryAll(RoleQueryCriteria criteria) {
        return roleMapper.toDto(roleRepository.findAll(((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder))));
    }

    @Override
    public RoleDto findById(long id) {
        Role role = roleRepository.findById(id).orElseGet(Role::new);
        ValidationUtil.isNull(role.getId(), "Role", "id", id);
        return roleMapper.toDto(role);
    }

    @Override
    public List<RoleSmallDto> findByUsersId(Long id) {
        return roleSmallMapper.toDto(new ArrayList<>(roleRepository.findByUserId(id)));
    }

    @Override
    public Integer findByRoles(Set<Role> roleSet) {
        if (roleSet.size() == 0) {
            return Integer.MAX_VALUE;
        }
        Set<RoleDto> roleDtoSet = new HashSet<>();
        for (Role role : roleSet) {
            roleDtoSet.add(findById(role.getId()));
        }
        return Collections.min(roleDtoSet.stream().map(RoleDto::getLevel).collect(Collectors.toList()));
    }

    @Override
    public void download(List<RoleDto> roleDtoList, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (RoleDto roleDto : roleDtoList) {
            Map<String, Object> map = new LinkedHashMap<>(4);
            map.put("角色名称", roleDto.getName());
            map.put("角色级别", roleDto.getLevel());
            map.put("描述", roleDto.getDescription());
            map.put("创建日期", roleDto.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    @Cacheable(key = "'auth:' + #p0.id")
    public List<GrantedAuthority> mapToGrantedAuthorities(UserDto user) {
        Set<String> permissions = new HashSet<>();
        // 如果是管理员直接返回
        if (user.getIsAdmin()) {
            permissions.add("admin");
            return permissions.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
        }
        Set<Role> roles = roleRepository.findByUserId(user.getId());
        permissions = roles.stream().flatMap(role -> role.getMenus().stream())
                .filter(menu -> StringUtils.isNotBlank(menu.getPermission()))
                .map(Menu::getPermission).collect(Collectors.toSet());
        return permissions.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    @Override
    public void verification(Set<Long> ids) {
        if (userRepository.countByRoleIds(ids) > 0) {
            throw new BadRequestException("所选角色存在关联的用户，请解除关联再试");
        }
    }

    private void delCaches(Long id, List<User> users) {
        users = CollectionUtil.isEmpty(users) ? userRepository.findByRoleId(id) : users;
        if (CollectionUtil.isNotEmpty(users)) {
            users.forEach(user -> userCacheClean.cleanUserCache(user.getUsername()));
            Set<Long> userIds = users.stream().map(User::getId).collect(Collectors.toSet());
            redisUtils.delByKeys(CacheKey.DATA_USER, userIds);
            redisUtils.delByKeys(CacheKey.MENU_USER, userIds);
            redisUtils.delByKeys(CacheKey.ROLE_AUTH, userIds);
        }
        redisUtils.del(CacheKey.ROLE_ID + id);
    }
}
