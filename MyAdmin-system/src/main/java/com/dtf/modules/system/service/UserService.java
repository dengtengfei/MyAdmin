package com.dtf.modules.system.service;

import com.dtf.modules.system.domain.User;
import com.dtf.modules.system.service.dto.UserDto;
import com.dtf.modules.system.service.dto.UserQueryCriteria;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.awt.print.Pageable;
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
     * 根据id查询
     *
     * @param id
     * @return
     */
    UserDto findById(long id);
//
//    void create(User user);
//
//    void update(User user) throws Exception;
//
//    void delete(Set<Long> ids);

    UserDto findByName(String username);

//    void updatePass(String user, String encryptPassword);
//
//    Map<String, String> updateAvatar(MultipartFile file);
//
//    void updateEmail(String username, String email);
//
//    Object queryAll(UserQueryCriteria criteria, Pageable pageable);
//
//    List<UserDto> queryAll(UserQueryCriteria criterial);
//
//    void download(List<UserDto> queryAll, HttpServletResponse response) throws IOException;
//
//    void updateCenter(User user);
}
