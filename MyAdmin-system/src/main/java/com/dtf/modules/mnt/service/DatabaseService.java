package com.dtf.modules.mnt.service;

import com.dtf.modules.mnt.domain.Database;
import com.dtf.modules.mnt.service.dto.DatabaseDto;
import com.dtf.modules.mnt.service.dto.DatabaseQueryCriteria;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * 0 *
 * 1 * @Author:  deng.tengfei
 * 2 * @email:  imdtf@qq.com
 * 3 * @Date:  2021/7/27 22:24
 */
public interface DatabaseService {
    /**
     * 创建数据库
     *
     * @param dataBase \
     */
    void create(Database dataBase);

    /**
     * 删除
     *
     * @param ids \
     */
    void delete(Set<String> ids);

    /**
     * 修改数据库
     *
     * @param dataBase \
     */
    void update(Database dataBase);

    /**
     * 根据id查找
     *
     * @param id \
     * @return \
     */
    DatabaseDto findBydId(String id);

    /**
     * 查询全部
     *
     * @param criteria \
     * @param pageable \
     * @return \
     */
    Object queryAll(DatabaseQueryCriteria criteria, Pageable pageable);

    /**
     * 查询全部
     *
     * @param criteria \
     * @return \
     */
    List<DatabaseDto> queryAll(DatabaseQueryCriteria criteria);

    /**
     * 导出数据库数据
     *
     * @param databaseDtoList \
     * @param response        \
     * @throws IOException \
     */
    void download(List<DatabaseDto> databaseDtoList, HttpServletResponse response) throws IOException;

    /**
     * 测试数据库连接
     * @param database \
     * @return \
     */
    boolean testConnection(Database database);
}
