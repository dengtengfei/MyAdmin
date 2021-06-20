package com.dtf.modules.system.service.impl;

import com.dtf.exception.EntityNotFoundException;
import com.dtf.modules.system.domain.User;
import com.dtf.modules.system.repository.UserRepository;
import com.dtf.modules.system.service.UserService;
import com.dtf.modules.system.service.dto.UserDto;
import com.dtf.modules.system.service.mapstruct.UserMapper;
import com.dtf.utils.ValidationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


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

    @Override
    public UserDto findByName(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new EntityNotFoundException(User.class, "name", username);
        } else {
            return userMapper.toDTO(user);
        }
    }

    @Override
    @Cacheable(key = "'id:' + #p0")
    @Transactional(rollbackFor = Exception.class)
    public UserDto findById(long id) {
        User user = userRepository.findById(id).orElseGet(User::new);
        ValidationUtil.isNull(user.getId(), "User", "id", id);
        return userMapper.toDTO(user);
    }
}
