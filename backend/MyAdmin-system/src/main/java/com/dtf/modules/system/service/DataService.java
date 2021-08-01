package com.dtf.modules.system.service;

import com.dtf.modules.system.service.dto.UserDto;

import java.util.List;

/**
 * 0 *
 * 1 * @Author:  deng.tengfei
 * 2 * @email:  imdtf@qq.com
 * 3 * @Date:  2021/6/14 21:16
 */
public interface DataService {
    /**
     * 根据用户获取部门id列表
     * @param user \
     * @return \
     */
    List<Long> getDeptIds(UserDto user);
}
