package com.dtf.modules.mnt.service;

import com.dtf.modules.mnt.domain.App;
import com.dtf.modules.mnt.service.dto.AppDto;
import com.dtf.modules.mnt.service.dto.AppQueryCriteria;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * 0 *
 * 1 * @Author:  deng.tengfei
 * 2 * @email:  imdtf@qq.com
 * 3 * @Date:  2021/7/14 19:34
 */
public interface AppService {
    /**
     * 新增应用
     *
     * @param app \
     */
    void create(App app);

    /**
     * 删除应用
     *
     * @param ids \
     */
    void delete(Set<Long> ids);

    /**
     * 修改应用
     *
     * @param app \
     */
    void update(App app);

    /**
     * 查询全部
     *
     * @param criteria \
     * @param pageable \
     * @return \
     */
    Object queryAll(AppQueryCriteria criteria, Pageable pageable);

    /**
     * 查询全部
     *
     * @param criteria \
     * @return \
     */
    List<AppDto> queryAll(AppQueryCriteria criteria);

    /**
     * 应用详情
     *
     * @param id \
     * @return \
     */
    AppDto findById(Long id);

    /**
     * 导出应用数据
     *
     * @param appDtoList \
     * @param response   \
     * @throws IOException \
     */
    void download(List<AppDto> appDtoList, HttpServletResponse response) throws IOException;
}
