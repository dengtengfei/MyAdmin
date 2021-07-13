package com.dtf.config;

import com.dtf.utils.SecurityUtils;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 0 *
 * 1 * @Author:  deng.tengfei
 * 2 * @email:  imdtf@qq.com
 * 3 * @Date:  2021/7/13 22:52
 */
@Component("auditorAware")
public class AuditorConfig implements AuditorAware<String> {
    @Override
    public Optional<String> getCurrentAuditor() {
        try {
            return Optional.of(SecurityUtils.getCurrentUsername());
        } catch (Exception ignored) {}
        // 用户定时任务，或者无Token调用的情况
        return Optional.of("System");
    }
}
