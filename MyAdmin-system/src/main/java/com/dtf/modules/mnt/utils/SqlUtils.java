package com.dtf.modules.mnt.utils;

import com.alibaba.druid.pool.DruidDataSource;
import com.dtf.utils.CloseUtils;
import com.dtf.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * 0 *
 * 1 * @Author:  deng.tengfei
 * 2 * @email:  imdtf@qq.com
 * 3 * @Date:  2021/7/27 22:47
 */
@Slf4j
public class SqlUtils {
    private static DataSource getDataSource(String jdbcUrl, String username, String pwd) {
        DruidDataSource druidDataSource = new DruidDataSource();
        String className;
        try {
            className = DriverManager.getDriver(jdbcUrl.trim()).getClass().getName();
        } catch (SQLException e) {
            throw new RuntimeException("Get driver class name error: " + jdbcUrl);
        }
        if (StringUtils.isBlank(className)) {
            DataTypeEnum dataTypeEnum = DataTypeEnum.urlOf(jdbcUrl);
            if (null == dataTypeEnum) {
                throw new RuntimeException("Not supported data type, jdbcUrl: " + jdbcUrl);
            }
            druidDataSource.setDriverClassName(dataTypeEnum.getDriver());
        } else {
            druidDataSource.setDriverClassName(className);
        }

        druidDataSource.setUrl(jdbcUrl);
        druidDataSource.setUsername(username);
        druidDataSource.setPassword(pwd);

        druidDataSource.setMaxWait(3000);
        druidDataSource.setInitialSize(1);
        druidDataSource.setMinIdle(1);
        druidDataSource.setMaxActive(1);
        druidDataSource.setBreakAfterAcquireFailure(true);

        try {
            druidDataSource.init();
        } catch (SQLException e) {
            log.error("Exception during pool initialization", e);
            throw new RuntimeException(e.getMessage());
        }
        return druidDataSource;
    }

    private static Connection getConnection(String jdbcUrl, String username, String pwd) {
        DataSource dataSource = getDataSource(jdbcUrl, username, pwd);
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
        } catch (Exception ignored) {
        }
        try {
            int timeOut = 5;
            if (null == connection || connection.isClosed() || !connection.isValid(timeOut)) {
                log.info("connection is not available, retry get connection.");
                connection = dataSource.getConnection();
            }
        } catch (Exception e) {
            log.error("create connection error, jdbcUrl: {}", jdbcUrl);
            throw new RuntimeException("create connection error, jdbcUrl: " + jdbcUrl);
        } finally {
            CloseUtils.close(connection);
        }

        return connection;
    }

    public static boolean testConnection(String jdbcUrl, String username, String pwd) {
        Connection connection = null;
        try {
            connection = getConnection(jdbcUrl, username, pwd);
            if (null != connection) {
                return true;
            }
        } catch (Exception e) {
            log.error("Get connection failed", e);
        } finally {
            CloseUtils.close(connection);
        }
        return false;
    }
}
