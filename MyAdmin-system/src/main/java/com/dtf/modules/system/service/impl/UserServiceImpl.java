package com.dtf.modules.system.service.impl;

import com.dtf.exception.EntityExistException;
import com.dtf.exception.EntityNotFoundException;
import com.dtf.modules.security.service.OnlineUserService;
import com.dtf.modules.security.service.UserCacheClean;
import com.dtf.modules.system.domain.User;
import com.dtf.modules.system.repository.UserRepository;
import com.dtf.modules.system.service.UserService;
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

import java.util.Date;
import java.util.Set;


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

    private void delCaches(Long id, String username) {
        redisUtils.del(CacheKey.USER_ID + id);
        flushCache(username);
    }

    private void flushCache(String username) {
        userCacheClean.cleanUserCache(username);
    }
}