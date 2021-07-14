package com.dtf.modules.mnt.service;

import com.dtf.modules.mnt.domain.Deploy;
import com.dtf.modules.mnt.service.dto.DeployDto;
import com.dtf.modules.mnt.service.dto.DeployQueryCriteria;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * 0 *
 * 1 * @Author:  deng.tengfei
 * 2 * @email:  imdtf@qq.com
 * 3 * @Date:  2021/7/14 20:53
 */
public interface DeployService {
    /**
     * 创建应用部署
     *
     * @param deploy \
     */
    void create(Deploy deploy);

    /**
     * 删除应用部署
     *
     * @param ids \
     */
    void delete(Set<Long> ids);

    /**
     * 更新应用部署
     *
     * @param deploy \
     */
    void update(Deploy deploy);

    /**
     * 查询全部
     *
     * @param criteria \
     * @param pageable \
     * @return \
     */
    Object queryAll(DeployQueryCriteria criteria, Pageable pageable);

    /**
     * 查询全部
     *
     * @param criteria \
     * @return \
     */
    List<DeployDto> queryAll(DeployQueryCriteria criteria);

    /**
     * 查询应用部署详情
     *
     * @param id \
     * @return \
     */
    DeployDto findById(Long id);

    /**
     * 导出应用部署数据
     *
     * @param deployDtoList \
     * @param response      \
     * @throws IOException \
     */
    void download(List<DeployDto> deployDtoList, HttpServletResponse response) throws IOException;

    /**
     * 部署文件到服务器
     * @param fileSavePath \
     * @param appId \
     */
    void deploy(String fileSavePath, Long id);

    /**
     * 发送部署结果
     * @param result \
     * @param stringBuilder \
     */
    void sendResultMsg(boolean result, StringBuilder stringBuilder);
}
