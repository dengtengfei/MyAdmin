package com.dtf.modules.mnt.service.impl;

import com.dtf.modules.mnt.domain.DeployHistory;
import com.dtf.modules.mnt.repository.DeployHistoryRepository;
import com.dtf.modules.mnt.service.DeployHistoryService;
import com.dtf.modules.mnt.service.dto.DeployHistoryDto;
import com.dtf.modules.mnt.service.dto.DeployHistoryQueryCriteria;
import com.dtf.modules.mnt.service.mapstruct.DeployHistoryMapper;
import com.dtf.utils.FileUtil;
import com.dtf.utils.PageUtil;
import com.dtf.utils.QueryHelp;
import com.dtf.utils.ValidationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * 0 *
 * 1 * @Author:  deng.tengfei
 * 2 * @email:  imdtf@qq.com
 * 3 * @Date:  2021/7/15 21:16
 */
@Service
@RequiredArgsConstructor
public class DeployHistoryServiceImpl implements DeployHistoryService {
    private final DeployHistoryRepository deployHistoryRepository;
    private final DeployHistoryMapper deployHistoryMapper;

    @Override
    public void create(DeployHistory deployHistory) {
        deployHistoryRepository.save(deployHistory);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Set<String> ids) {
        deployHistoryRepository.deleteAllByIdIn(ids);
    }

    @Override
    public DeployHistoryDto findById(String id) {
        DeployHistory deployHistory = deployHistoryRepository.findById(id).orElseGet(DeployHistory::new);
        ValidationUtil.isNull(deployHistory.getId(), "DeployHistory", "id", id);
        return deployHistoryMapper.toDto(deployHistory);
    }

    @Override
    public Object queryAll(DeployHistoryQueryCriteria criteria, Pageable pageable) {
        Page<DeployHistory> page = deployHistoryRepository.findAll((root, criteriaQuery, cb) -> QueryHelp.getPredicate(root, criteria, cb), pageable);
        return PageUtil.toPage(page.map(deployHistoryMapper::toDto));
    }

    @Override
    public List<DeployHistoryDto> queryAll(DeployHistoryQueryCriteria criteria) {
        return deployHistoryMapper.toDto(deployHistoryRepository.findAll((root, criteriaQuery, cb) -> QueryHelp.getPredicate(root, criteria, cb)));
    }

    @Override
    public void download(List<DeployHistoryDto> deployHistoryDtoList, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (DeployHistoryDto deployHistoryDto : deployHistoryDtoList) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("部署编号", deployHistoryDto.getDeployId());
            map.put("应用名称", deployHistoryDto.getAppName());
            map.put("部署IP", deployHistoryDto.getIp());
            map.put("部署时间", deployHistoryDto.getDeployDate());
            map.put("部署人员", deployHistoryDto.getDeployUser());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}
