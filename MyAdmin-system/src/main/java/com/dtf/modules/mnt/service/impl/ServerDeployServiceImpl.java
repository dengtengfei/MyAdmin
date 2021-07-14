package com.dtf.modules.mnt.service.impl;

import com.dtf.modules.mnt.domain.ServerDeploy;
import com.dtf.modules.mnt.repository.ServerDeployRepository;
import com.dtf.modules.mnt.service.ServerDeployService;
import com.dtf.modules.mnt.service.dto.ServerDeployDto;
import com.dtf.modules.mnt.service.dto.ServerDeployQueryCriteria;
import com.dtf.modules.mnt.service.mapstruct.ServerDeployMapper;
import com.dtf.modules.mnt.utils.ExecuteShellUtil;
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
 * 3 * @Date:  2021/7/13 23:28
 */
@Service
@RequiredArgsConstructor
public class ServerDeployServiceImpl implements ServerDeployService {
    private final ServerDeployRepository serverDeployRepository;
    private final ServerDeployMapper serverDeployMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(ServerDeploy serverDeploy) {
        serverDeployRepository.save(serverDeploy);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Set<Long> ids) {
        serverDeployRepository.deleteAllByIdIn(ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(ServerDeploy serverDeploy) {
        ServerDeploy oldServerDeploy = serverDeployRepository.findById(serverDeploy.getId()).orElseGet(ServerDeploy::new);
        ValidationUtil.isNull(oldServerDeploy.getId(), "ServerDeploy", "id", serverDeploy.getId());
        oldServerDeploy.copy(serverDeploy);
        serverDeployRepository.save(oldServerDeploy);
    }

    @Override
    public Object queryAll(ServerDeployQueryCriteria criteria, Pageable pageable) {
        Page<ServerDeploy> pages = serverDeployRepository.findAll(((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder)), pageable);
        return PageUtil.toPage(pages.map(serverDeployMapper::toDto));
    }

    @Override
    public List<ServerDeployDto> queryAll(ServerDeployQueryCriteria criteria) {
        return serverDeployMapper.toDto(serverDeployRepository.findAll(((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder))));
    }

    @Override
    public void download(List<ServerDeployDto> serverDeployDtoList, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (ServerDeployDto deployDto : serverDeployDtoList) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("服务器名称", deployDto.getName());
            map.put("服务器IP", deployDto.getIp());
            map.put("端口", deployDto.getPort());
            map.put("账号", deployDto.getAccount());
            map.put("创建日期", deployDto.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public boolean testConnect(ServerDeploy serverDeploy) {
        ExecuteShellUtil executeShellUtil = null;
        try {
            executeShellUtil = new ExecuteShellUtil(serverDeploy.getIp(), serverDeploy.getAccount(), serverDeploy.getPassword(), serverDeploy.getPort());
            return executeShellUtil.execute("ls") == 0;
        } catch (Exception e) {
            return false;
        } finally {
            if (executeShellUtil != null) {
                executeShellUtil.close();
            }
        }
    }
}
