package com.dtf.modules.security.service;

import com.dtf.exception.EntityNotFoundException;
import com.dtf.modules.security.config.bean.LoginProperties;
import com.dtf.modules.security.service.dto.JwtUserDto;
import com.dtf.modules.system.service.DataService;
import com.dtf.modules.system.service.RoleService;
import com.dtf.modules.system.service.UserService;
import com.dtf.modules.system.service.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
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
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserService userService;
    private final RoleService roleService;
    private final DataService dataService;
    private final LoginProperties loginProperties;
    static Map<String, JwtUserDto> userDtoCache = new ConcurrentHashMap<>();

    public void setEnableCache(boolean enableCache) {
        this.loginProperties.setCacheEnabled(enableCache);
    }

    @Override
    public JwtUserDto loadUserByUsername(String username) {
        boolean searchDb = true;
        JwtUserDto jwtUserDto = null;
        if (loginProperties.isCacheEnabled() && userDtoCache.containsKey(username)) {
            jwtUserDto = userDtoCache.get(username);
            List<Long> dataScopes = jwtUserDto.getDataScopes();
            dataScopes.clear();
            dataScopes.addAll(dataService.getDeptIds(jwtUserDto.getUser()));
            searchDb = false;
        }
        if (searchDb) {
            UserDto user;
            try {
                user = userService.findByName(username);
            } catch (EntityNotFoundException e) {
                // SpringSecurity会自动转换UsernameNotFoundException为BadCredentialsException
                throw new UsernameNotFoundException("", e);
            }
            if (user == null) {
                throw new UsernameNotFoundException("");
            } else {
                if (!user.getEnabled()) {
                    throw new UsernameNotFoundException("账号未激活");
                }
                jwtUserDto = new JwtUserDto(user, dataService.getDeptIds(user), roleService.mapToGrantedAuthorities(user));
                userDtoCache.put(username, jwtUserDto);
            }
        }
        return jwtUserDto;
    }
}
