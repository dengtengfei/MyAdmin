package com.dtf.modules.system.service.impl;

import com.dtf.config.FileProperties;
import com.dtf.exception.BadRequestException;
import com.dtf.exception.EntityExistException;
import com.dtf.exception.EntityNotFoundException;
import com.dtf.modules.security.service.OnlineUserService;
import com.dtf.modules.security.service.UserCacheClean;
import com.dtf.modules.system.domain.User;
import com.dtf.modules.system.repository.UserRepository;
import com.dtf.modules.system.service.UserService;
import com.dtf.modules.system.service.dto.JobSmallDto;
import com.dtf.modules.system.service.dto.RoleSmallDto;
import com.dtf.modules.system.service.dto.UserDto;
import com.dtf.modules.system.service.dto.UserQueryCriteria;
import com.dtf.modules.system.service.mapstruct.UserMapper;
import com.dtf.utils.*;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


/**
 * 0 *
 * 1 * @Author:  deng.tengfei
 * 2 * @email:  imdtf@qq.com
 * 3 * @Date:  2021/6/14 21:51
 */
@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "user")
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final FileProperties properties;
    private final RedisUtils redisUtils;
    private final UserCacheClean userCacheClean;
    private final OnlineUserService onlineUserService;

    @Override
    public void create(User user) {
        if (userRepository.findByUsername(user.getUsername()) != null) {
            throw new EntityExistException(User.class, "username", user.getUsername());
        }
        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new EntityExistException(User.class, "email", user.getEmail());
        }
        if (userRepository.findByPhone(user.getPhone()) != null) {
            throw new EntityExistException(User.class, "phone", user.getPhone());
        }
        userRepository.save(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Set<Long> ids) {
        for (Long id : ids) {
            UserDto user = findById(id);
            delCaches(user.getId(), user.getUsername());
        }
        userRepository.deleteAllByIdIn(ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(User user) throws Exception {
        User oldUser = userRepository.findById(user.getId()).orElseGet(User::new);
        ValidationUtil.isNull(oldUser.getId(), "user", "id", user.getId());
        User user1 = userRepository.findByUsername(user.getUsername());
        User user2 = userRepository.findByEmail(user.getEmail());
        User user3 = userRepository.findByPhone(user.getPhone());
        if (user1 != null && !oldUser.getId().equals(user1.getId())) {
            throw new EntityExistException(User.class, "username", user.getUsername());
        }
        if (user2 != null && !oldUser.getId().equals(user2.getId())) {
            throw new EntityExistException(User.class, "email", user.getEmail());
        }
        if (user3 != null && !oldUser.getId().equals(user3.getId())) {
            throw new EntityExistException(User.class, "phone", user.getPhone());
        }
        if (!user.getRoles().equals(oldUser.getRoles())) {
            redisUtils.del(CacheKey.DATA_USER + user.getId());
            redisUtils.del(CacheKey.MENU_USER + user.getId());
            redisUtils.del(CacheKey.ROLE_AUTH + user.getId());
        }
        if (!user.getEnabled()) {
            onlineUserService.kickOutForUsername(user.getUsername());
        }
        oldUser.setUsername(user.getUsername());
        oldUser.setEmail(user.getEmail());
        oldUser.setEnabled(user.getEnabled());
        oldUser.setRoles(user.getRoles());
        oldUser.setDept(user.getDept());
        oldUser.setJobs(user.getJobs());
        oldUser.setPhone(user.getPhone());
        oldUser.setNickName(user.getNickName());
        oldUser.setGender(user.getGender());
        userRepository.save(oldUser);
        delCaches(user.getId(), user.getUsername());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCenter(User user) {
        User oldUser = userRepository.findById(user.getId()).orElseGet(User::new);
        User userByPhone = userRepository.findByPhone(user.getPhone());
        if (userByPhone != null && !oldUser.getId().equals(userByPhone.getId())) {
            throw new EntityExistException(User.class, "phone", user.getPhone());
        }
        oldUser.setNickName(user.getNickName());
        oldUser.setPhone(user.getPhone());
        oldUser.setGender(user.getGender());
        userRepository.save(oldUser);

        delCaches(user.getId(), user.getUsername());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePass(String username, String encryptPassword) {
        userRepository.updatePass(username, encryptPassword, new Date());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateEmail(String username, String email) {
        userRepository.updateEmail(username, email);
        flushCache(username);
    }

    @Override
    public Map<String, String> updateAvatar(MultipartFile multipartFile) {
        FileUtil.checkSize(properties.getAvatarMaxSize(), multipartFile.getSize());

        String imageType = "gif jpg png jpeg";
        String fileType = FileUtil.getExtensionName(multipartFile.getOriginalFilename());
        if (fileType != null && !imageType.contains(fileType)) {
            throw new BadRequestException("文件格式错误，仅支持 " + imageType + " 格式");
        }
        User user = userRepository.findByUsername(SecurityUtils.getCurrentUsername());
        String oldPath = user.getAvatarPath();
        File file = FileUtil.upload(multipartFile, properties.getPath().getAvatar());
        user.setAvatarPath(Objects.requireNonNull(file).getPath());
        user.setAvatarName(file.getName());
        userRepository.save(user);
        if (StringUtils.isNotBlank(oldPath)) {
            FileUtil.del(oldPath);
        }
        @NotBlank String username = user.getUsername();
        flushCache(username);
        return new HashMap<String, String>(1) {{
            put("avatar", file.getName());
        }};
    }

    @Override
    public UserDto findByName(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new EntityNotFoundException(User.class, "name", username);
        } else {
            return userMapper.toDto(user);
        }
    }

    @Override
    @Cacheable(key = "'id:' + #p0")
    @Transactional(rollbackFor = Exception.class)
    public UserDto findById(long id) {
        User user = userRepository.findById(id).orElseGet(User::new);
        ValidationUtil.isNull(user.getId(), "User", "id", id);
        return userMapper.toDto(user);
    }

    @Override
    public Object queryAll(UserQueryCriteria criteria, Pageable pageable) {
        Page<User> page = userRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder), pageable);
        return PageUtil.toPage(page.map(userMapper::toDto));
    }

    @Override
    public List<UserDto> queryAll(UserQueryCriteria criteria) {
        List<User> users = userRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder));
        return userMapper.toDto(users);
    }

    @Override
    public void download(List<UserDto> userDtoList, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (UserDto userDto : userDtoList) {
            List<String> roles = userDto.getRoles().stream().map(RoleSmallDto::getName).collect(Collectors.toList());
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("用户名", userDto.getUsername());
            map.put("角色", roles);
            map.put("部门", userDto.getDept().getName());
            map.put("岗位", userDto.getJobs().stream().map(JobSmallDto::getName).collect(Collectors.toList()));
            map.put("邮箱", userDto.getEmail());
            map.put("状态", userDto.getEnabled() ? "启用" : "禁用");
            map.put("手机号码", userDto.getPhone());
            map.put("密码修改时间", userDto.getPwdResetTime());
            map.put("创建日期", userDto.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    private void delCaches(Long id, String username) {
        redisUtils.del(CacheKey.USER_ID + id);
        flushCache(username);
    }

    private void flushCache(String username) {
        userCacheClean.cleanUserCache(username);
    }
}