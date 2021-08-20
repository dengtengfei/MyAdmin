package com.dtf.utils;

/**
 * 0 *  @see {@link SpringContextHolder}
 *      针对某些初始化方法，在SpringContextHolder 初始化前时，<br>
 *      可提交一个 提交回调任务。<br>
 *      在SpringContextHolder 初始化后，进行回调使用
 * 1 * @Author:  deng.tengfei
 * 2 * @email:  imdtf@qq.com
 * 3 * @Date:  2021/6/12 0:40
 */
public interface CallBack {
    /**
     * 回调执行方法
     */
    void executor();

    /**
     * 本次回调任务名称
     * @return \
     */
    default String getCallBackName() {
        return Thread.currentThread().getId() + ":" + this.getClass().getName();
    }
}
