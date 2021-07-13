package com.dtf.modules.mnt.service;

import com.dtf.modules.mnt.domain.ServerDeploy;
import com.dtf.modules.mnt.service.dto.ServerDeployDto;
import com.dtf.modules.mnt.service.dto.ServerDeployQueryCriteria;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * 0 *
 * 1 * @Author:  deng.tengfei
 * 2 * @email:  imdtf@qq.com
 * 3 * @Date:  2021/7/13 23:13
 */
public interface ServerDeployService {
    /**
     * 添加服务器
     *
     * @param serverDeploy \
     */
    void create(ServerDeploy serverDeploy);

    /**
     * 删除服务器
     *
     * @param ids \
     */
    void delete(Set<Long> ids);

    /**
     * 修改服务器
     *
     * @param serverDeploy \
     */
    void update(ServerDeploy serverDeploy);

    /**
     * 查询全部
     *
     * @param criteria \
     * @param pageable \
     * @return \
     */
    Object queryAll(ServerDeployQueryCriteria criteria, Pageable pageable);

    /**
     * 查询全部
     *
     * @param criteria \
     * @return \
     */
    List<ServerDeployDto> queryAll(ServerDeployQueryCriteria criteria);

    /**
     * 导出服务器数据
     *
     * @param serverDeployDtoList \
     * @param response            \
     * @throws IOException \
     */
    void download(List<ServerDeployDto> serverDeployDtoList, HttpServletResponse response) throws IOException;
}
