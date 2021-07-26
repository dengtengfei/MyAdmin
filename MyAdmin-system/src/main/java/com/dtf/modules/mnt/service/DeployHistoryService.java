package com.dtf.modules.mnt.service;

import com.dtf.modules.mnt.domain.DeployHistory;
import com.dtf.modules.mnt.service.dto.DeployHistoryDto;
import com.dtf.modules.mnt.service.dto.DeployHistoryQueryCriteria;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * 0 *
 * 1 * @Author:  deng.tengfei
 * 2 * @email:  imdtf@qq.com
 * 3 * @Date:  2021/7/15 21:16
 */
public interface DeployHistoryService {
    /**
     * 创建部署历史
     *
     * @param deployHistory \
     */
    void create(DeployHistory deployHistory);

    /**
     * 删除部署历史
     *
     * @param ids \
     */
    void delete(Set<String> ids);

    /**
     * 根据id查询
     * @param id \
     * @return \
     */
    DeployHistoryDto findById(String id);

    /**
     * 查询全部部署历史记录
     *
     * @param criteria \
     * @param pageable \
     * @return \
     */
    Object queryAll(DeployHistoryQueryCriteria criteria, Pageable pageable);

    /**
     * 查询全部部署历史
     *
     * @param criteria \
     * @return \
     */
    List<DeployHistoryDto> queryAll(DeployHistoryQueryCriteria criteria);

    /**
     * 导出部署历史记录
     *
     * @param deployHistoryDtoList \
     * @param response             \
     * @throws IOException \
     */
    void download(List<DeployHistoryDto> deployHistoryDtoList, HttpServletResponse response) throws IOException;
}
