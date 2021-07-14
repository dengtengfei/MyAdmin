package com.dtf.modules.mnt.service.impl;

import com.dtf.exception.BadRequestException;
import com.dtf.modules.mnt.domain.App;
import com.dtf.modules.mnt.repository.AppRepository;
import com.dtf.modules.mnt.service.AppService;
import com.dtf.modules.mnt.service.dto.AppDto;
import com.dtf.modules.mnt.service.dto.AppQueryCriteria;
import com.dtf.modules.mnt.service.mapstruct.AppMapper;
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
 * 3 * @Date:  2021/7/14 19:34
 */
@Service
@RequiredArgsConstructor
public class AppServiceImpl implements AppService {
    private final AppRepository appRepository;
    private final AppMapper appMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(App app) {
        verification(app);
        appRepository.save(app);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Set<Long> ids) {
        appRepository.deleteAllByIdIn(ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(App app) {
        verification(app);
        App oldApp = appRepository.findById(app.getId()).orElseGet(App::new);
        ValidationUtil.isNull(oldApp.getId(), "App", "id", app.getId());
        oldApp.copy(app);
        appRepository.save(oldApp);
    }

    @Override
    public Object queryAll(AppQueryCriteria criteria, Pageable pageable) {
        Page<App> pages = appRepository.findAll(((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder)), pageable);
        return PageUtil.toPage(pages.map(appMapper::toDto));
    }

    @Override
    public List<AppDto> queryAll(AppQueryCriteria criteria) {
        return appMapper.toDto(appRepository.findAll(((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder))));
    }

    @Override
    public AppDto findById(Long id) {
        App app = appRepository.findById(id).orElseGet(App::new);
        ValidationUtil.isNull(app.getId(), "App", "id", id);
        return appMapper.toDto(app);
    }

    @Override
    public void download(List<AppDto> appDtoList, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (AppDto appDto : appDtoList) {
            Map<String, Object> map = new LinkedHashMap<>(8);
            map.put("应用名称", appDto.getName());
            map.put("端口", appDto.getPort());
            map.put("上传目录", appDto.getUploadPath());
            map.put("部署目录", appDto.getDeployPath());
            map.put("备份目录", appDto.getBackupPath());
            map.put("启动脚本", appDto.getStartScript());
            map.put("部署脚本", appDto.getDeployScript());
            map.put("创建日期", appDto.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    private void verification(App app) {
        String opt = "/opt", home = "/home";
        if (!(app.getUploadPath().startsWith(opt) || app.getUploadPath().startsWith(home))) {
            throw new BadRequestException("文件只能上传到/opt目录或者/home目录");
        }
        if (!(app.getDeployPath().startsWith(opt) || app.getDeployPath().startsWith(home))) {
            throw new BadRequestException("文件只能部署到/opt目录或者/home目录");
        }
        if (!(app.getBackupPath().startsWith(opt) || app.getBackupPath().startsWith(home))) {
            throw new BadRequestException("文件只能备份到/opt目录或者/home目录");
        }
    }
}
