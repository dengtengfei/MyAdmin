package com.dtf.config;

import com.dtf.utils.Constant;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Locale;

/**
 * 0 *
 * 1 * @Author:  deng.tengfei
 * 2 * @email:  imdtf@qq.com
 * 3 * @Date:  2021/6/14 16:43
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "file")
public class FileProperties {
    private Long maxSize;

    private Long avatarMaxSize;

    private MyPath mac;

    private MyPath linux;

    private MyPath windows;

    public MyPath getPath() {
        String os = System.getProperty("os.name");
        if (os.toLowerCase().startsWith(Constant.WIN)) {
            return windows;
        } else if (os.toLowerCase().startsWith(Constant.MAC)) {
            return linux;
        }
        return linux;
    }

    @Data
    public static class MyPath {
        private String path;
        private String avatar;
    }
}
