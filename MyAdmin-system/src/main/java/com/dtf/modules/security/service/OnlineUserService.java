package com.dtf.modules.security.service;

import com.dtf.modules.security.config.bean.SecurityProperties;
import com.dtf.modules.security.service.dto.JwtUserDto;
import com.dtf.modules.security.service.dto.OnlineUserDto;
import com.dtf.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * 0 *
 * 1 * @Author:  deng.tengfei
 * 2 * @email:  imdtf@qq.com
 * 3 * @Date:  2021/6/14 0:02
 */
@Service
@Slf4j
public class OnlineUserService {
    private final SecurityProperties properties;
    private final RedisUtils redisUtils;

    public OnlineUserService(SecurityProperties properties, RedisUtils redisUtils) {
        this.properties = properties;
        this.redisUtils = redisUtils;
    }

    public void save(JwtUserDto jwtUserDto, String token, HttpServletRequest request) {
        String dept = jwtUserDto.getUser().getDept().getName();
        String ip = StringUtils.getIp(request);
        String browser = StringUtils.getBrowser(request);
        String address = StringUtils.getCityInfo(ip);
        OnlineUserDto onlineUserDto = null;
        try {
            onlineUserDto = new OnlineUserDto(jwtUserDto.getUsername(), jwtUserDto.getUser().getNickName(), dept, browser, ip, address, EncryptUtils.desEncrypt(token), new Date());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        redisUtils.set(properties.getOnlineKey() + token, onlineUserDto, properties.getTokenValidityInSeconds() / 1000);
    }

    public Map<String, Object> getAll(String filter, Pageable pageable) {
        List<OnlineUserDto> onlineUserDtoList = getAll(filter);
        return PageUtil.toPage(PageUtil.toPage(pageable.getPageNumber(), pageable.getPageSize(), onlineUserDtoList),
                onlineUserDtoList.size());
    }

    public List<OnlineUserDto> getAll(String filter) {
        List<String> keys = redisUtils.scan(properties.getOnlineKey() + "*");
        Collections.reverse(keys);
        List<OnlineUserDto> onlineUserDtoList = new ArrayList<>();
        for (String key : keys) {
            OnlineUserDto onlineUserDto = (OnlineUserDto) redisUtils.get(key);
            if (StringUtils.isNotBlank(filter)) {
                if (onlineUserDto.toString().contains(filter)) {
                    onlineUserDtoList.add(onlineUserDto);
                }
            } else {
                onlineUserDtoList.add(onlineUserDto);
            }
        }
        onlineUserDtoList.sort((o1, o2) -> o2.getLoginTime().compareTo(o1.getLoginTime()));
        return onlineUserDtoList;
    }

    @Async
    public void kickOutForUsername(String username) throws Exception {
        List<OnlineUserDto> onlineUserDtoList = getAll(username);
        for (OnlineUserDto onlineUserDto : onlineUserDtoList) {
            if (onlineUserDto.getUsername().equals(username)) {
                String token = EncryptUtils.desDecrypt(onlineUserDto.getKey());
                kickOut(token);
            }
        }
    }

    /**
     * 踢出用户
     * @param key /
     */
    public void kickOut(String key) {
        key = properties.getOnlineKey() + key;
        redisUtils.del(key);
    }

    public void logout(String token) {
        String key = properties.getOnlineKey() + token;
        redisUtils.del(key);
    }

    public void download(List<OnlineUserDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (OnlineUserDto user : all) {
            Map<String, Object> map = new LinkedHashMap<>(6);
            map.put("用户名", user.getUsername());
            map.put("部门", user.getDept());
            map.put("登录IP", user.getIp());
            map.put("登录地点", user.getAddress());
            map.put("浏览器", user.getBrowser());
            map.put("登录日期", user.getLoginTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    public OnlineUserDto getOne(String key) {
        return (OnlineUserDto) redisUtils.get(key);
    }

    public void checkLoginOnUser(String username, String ignoreToken) {
        List<OnlineUserDto> onlineUserDtoList = getAll(username);
        if (onlineUserDtoList == null || onlineUserDtoList.isEmpty()) {
            return;
        }
        for (OnlineUserDto onlineUserDto : onlineUserDtoList) {
            if (onlineUserDto.getUsername().equals(username)) {
                try {
                    String token = EncryptUtils.desDecrypt(onlineUserDto.getKey());
                    if (StringUtils.isNotBlank(ignoreToken) && !ignoreToken.equals(token)) {
                        this.kickOut(token);
                    } else if (StringUtils.isBlank(ignoreToken)) {
                        this.kickOut(token);
                    }
                } catch (Exception e) {
                    log.error("checkUser is error: ", e);
                }
            }
        }
    }
}
