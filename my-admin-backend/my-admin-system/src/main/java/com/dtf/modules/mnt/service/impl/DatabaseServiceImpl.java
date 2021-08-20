package com.dtf.modules.mnt.service.impl;

import com.dtf.modules.mnt.domain.Database;
import com.dtf.modules.mnt.repository.DatabaseRepository;
import com.dtf.modules.mnt.service.DatabaseService;
import com.dtf.modules.mnt.service.dto.DatabaseDto;
import com.dtf.modules.mnt.service.dto.DatabaseQueryCriteria;
import com.dtf.modules.mnt.service.mapstruct.DatabaseMapper;
import com.dtf.modules.mnt.utils.SqlUtils;
import com.dtf.utils.FileUtil;
import com.dtf.utils.PageUtil;
import com.dtf.utils.QueryHelp;
import com.dtf.utils.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
 * 3 * @Date:  2021/7/27 22:35
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DatabaseServiceImpl implements DatabaseService {
    private final DatabaseRepository databaseRepository;
    private final DatabaseMapper databaseMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(Database dataBase) {
        databaseRepository.save(dataBase);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Set<String> ids) {
        databaseRepository.deleteAllByIdIn(ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Database database) {
        Database oldDatabase = databaseRepository.findById(database.getId()).orElseGet(Database::new);
        ValidationUtil.isNull(oldDatabase.getId(), "Database", "id", database.getId());
        oldDatabase.copy(database);
        databaseRepository.save(oldDatabase);
    }

    @Override
    public DatabaseDto findBydId(String id) {
        Database database = databaseRepository.findById(id).orElseGet(Database::new);
        ValidationUtil.isNull(database.getId(), "Database", "id", id);
        return databaseMapper.toDto(database);
    }

    @Override
    public Object queryAll(DatabaseQueryCriteria criteria, Pageable pageable) {
        Page<Database> page = databaseRepository.findAll((root, criteriaQuery, cb) -> QueryHelp.getPredicate(root, criteria, cb), pageable);
        return PageUtil.toPage(page.map(databaseMapper::toDto));
    }

    @Override
    public List<DatabaseDto> queryAll(DatabaseQueryCriteria criteria) {
        return databaseMapper.toDto(databaseRepository.findAll((root, criteriaQuery, cb) -> QueryHelp.getPredicate(root, criteria, cb)));
    }

    @Override
    public void download(List<DatabaseDto> databaseDtoList, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (DatabaseDto databaseDto : databaseDtoList) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("数据库名称", databaseDto.getName());
            map.put("数据库连接地址", databaseDto.getJdbcUrl());
            map.put("用户名", databaseDto.getUserName());
            map.put("创建日期", databaseDto.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public boolean testConnection(Database database) {
        try {
            return SqlUtils.testConnection(database.getJdbcUrl(), database.getUserName(), database.getPwd());
        } catch (Exception e) {
            log.error("Test connection failed", e);
        }

        return false;
    }
}
