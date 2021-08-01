package com.dtf.config;

import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 0 *
 * 1 * @Author:  deng.tengfei
 * 2 * @email:  imdtf@qq.com
 * 3 * @Date:  2021/7/10 21:38
 */
@Component
public class ThreadFactoryName implements ThreadFactory {
    private static final AtomicInteger POOL_NUMBER = new AtomicInteger(1);
    private final ThreadGroup group;
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final String namePrefix;

    public ThreadFactoryName() {
        this("dtf-pool");
    }

    private ThreadFactoryName(String name) {
        // TODO need study
        SecurityManager securityManager = System.getSecurityManager();
        group = (securityManager != null) ? securityManager.getThreadGroup() : Thread.currentThread().getThreadGroup();
        //此时namePrefix就是 name + 第几个用这个工厂创建线程池的
        this.namePrefix = name + POOL_NUMBER.getAndIncrement();
    }

    @Override
    public Thread newThread(Runnable runnable) {
        //此时线程的名字 就是 namePrefix + -thread- + 这个线程池中第几个执行的线程
        Thread thread = new Thread(group, runnable, namePrefix + "-thread-" + threadNumber.getAndIncrement(), 0);
        if (thread.isDaemon()) {
            thread.setDaemon(false);
        }
        if (thread.getPriority() != Thread.NORM_PRIORITY) {
            thread.setPriority(Thread.NORM_PRIORITY);
        }
        return thread;
    }
}
