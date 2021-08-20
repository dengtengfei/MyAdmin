package com.dtf.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;

import java.util.ArrayList;
import java.util.List;

/**
 * 0 *
 * 1 * @Author:  deng.tengfei
 * 2 * @email:  imdtf@qq.com
 * 3 * @Date:  2021/6/12 0:38
 */
@Slf4j
public class SpringContextHolder implements ApplicationContextAware, DisposableBean {
    private static ApplicationContext applicationContext = null;
    private static final List<CallBack> CALL_BACK_LIST = new ArrayList<>();
    private static boolean addCallback = true;

    public synchronized static void addCallBacks(CallBack callBack) {
        if (addCallback) {
            SpringContextHolder.CALL_BACK_LIST.add(callBack);
        } else {
            log.warn("CallBack: {} 已无法添加！立即执行", callBack.getCallBackName());
            callBack.executor();
        }
    }

    public static <T> T getBean(String name) {
        assertContextInjected();
        return (T) applicationContext.getBean(name);
    }

    public static <T> T getBean(Class<T> requiredType) {
        assertContextInjected();
        return applicationContext.getBean(requiredType);
    }

    public static <T> T getProperties(String property, T defaultValue, Class<T> requiredType) {
        T result = defaultValue;
        try {
            result = getBean(Environment.class).getProperty(property, requiredType);
        } catch (Exception ignored) {}
        return result;
    }

    private static void assertContextInjected() {
        if (applicationContext == null) {
            throw new IllegalStateException("applicationContext属性未注入，请在applicationContext.mxl中定义SpringContextHolder或在SpringBoot启动类中注册SpringContextHolder.");
        }
    }

    private static void clearHolder() {
        log.debug("清除SpringContextHolder中的ApplicationContext:" + applicationContext);
        applicationContext = null;
    }

    @Override
    public void destroy() {
        SpringContextHolder.clearHolder();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        if (SpringContextHolder.applicationContext != null) {
            log.warn("SpringContextHolder中的ApplicationContext被覆盖, 原有ApplicationContext为:" + SpringContextHolder.applicationContext);
        }
        SpringContextHolder.applicationContext = applicationContext;
        if (addCallback) {
            for (CallBack callBack : SpringContextHolder.CALL_BACK_LIST) {
                callBack.executor();
            }
            CALL_BACK_LIST.clear();
        }
        SpringContextHolder.addCallback = false;
    }
}
