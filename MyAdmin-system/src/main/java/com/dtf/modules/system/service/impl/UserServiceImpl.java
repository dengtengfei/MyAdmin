package com.dtf.modules.system.service.impl;

import com.dtf.exception.EntityNotFoundException;
import com.dtf.modules.system.domain.User;
import com.dtf.modules.system.repository.UserRepository;
import com.dtf.modules.system.service.UserService;
import com.dtf.modules.system.service.dto.UserDto;
import com.dtf.modules.system.service.mapstruct.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;



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
}
