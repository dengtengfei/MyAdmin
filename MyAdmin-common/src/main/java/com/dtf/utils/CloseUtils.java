package com.dtf.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;

/**
 * 0 *
 * 1 * @Author:  deng.tengfei
 * 2 * @email:  imdtf@qq.com
 * 3 * @Date:  2021/7/27 23:06
 */
@Slf4j
public class CloseUtils {
    public static void close(Closeable closeable) {
        if (null != closeable) {
            try {
                closeable.close();
            } catch (Exception e) {
                log.error("Close connection failed", e);
            }
        }
    }

    public static void close(AutoCloseable autoCloseable) {
        if (null != autoCloseable) {
            try {
                autoCloseable.close();
            } catch (Exception e) {
                log.error("Close connection failed", e);
            }
        }
    }
}
