package com.dtf.modules.system.service;

import com.dtf.modules.system.domain.User;
import com.dtf.modules.system.service.dto.UserDto;
import com.dtf.modules.system.service.dto.UserQueryCriteria;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 0 *
 * 1 * @Author:  deng.tengfei
 * 2 * @email:  imdtf@qq.com
 * 3 * @Date:  2021/6/14 21:03
 */
public interface UserService {
    /**
     * 创建用户
     * @param user \
     */
    void create(User user);

    /**
     * 删除用户
     * @param ids \
     */
    void delete(Set<Long> ids);

    /**
     * 修改用户
     * @param user \
     * @throws Exception \
     */
    void update(User user) throws Exception;

    /**
     * 更新自主修改资料
     * @param user
     */
    void updateCenter(User user);

    /**
     * 修改密码
     * @param username \
     * @param encryptPassword \
     */
    void updatePass(String username, String encryptPassword);

    /**
     * 修改邮箱
     * @param username \
     * @param email \
     */
    void updateEmail(String username, String email);

    /**
     * 根据id查询
     *
     * @param id \
     * @return \
     */
    UserDto findById(long id);

    /**
     * 根据用户名查询
     * @param username \
     * @return \
     */
    UserDto findByName(String username);
//
//    Map<String, String> updateAvatar(MultipartFile file);

    /**
     * 根据条件查询所有
     * @param criteria \
     * @param pageable \
     * @return \
     */
    Object queryAll(UserQueryCriteria criteria, Pageable pageable);
//
//    List<UserDto> queryAll(UserQueryCriteria criterial);
//
//    void download(List<UserDto> queryAll, HttpServletResponse response) throws IOException;
}
