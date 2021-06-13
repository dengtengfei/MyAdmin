package com.dtf.modules.security.service;

import com.dtf.modules.security.service.dto.JwtUserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 0 *
 * 1 * @Author:  deng.tengfei
 * 2 * @email:  imdtf@qq.com
 * 3 * @Date:  2021/6/14 1:16
 */
@RequiredArgsConstructor
@Service("userDetailService")
public class UserDetailsServiceImpl implements Serializable {
    static Map<String , JwtUserDto> userDtoCache = new ConcurrentHashMap<>();
}
