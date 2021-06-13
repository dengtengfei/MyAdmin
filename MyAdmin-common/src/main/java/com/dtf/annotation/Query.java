package com.dtf.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 0 *
 * 1 * @Author:  deng.tengfei
 * 2 * @email:  imdtf@qq.com
 * 3 * @Date:  2021/6/12 0:23
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Query {
    /**
     * 基本对象的属性名
     * @return
     */
    String propName() default "";

    /**
     * 查询方式
     * @return
     */
    Type type() default Type.EQUAL;

    /**
     * 连接查询的属性名，如User类中的dept
     */
    String joinName() default "";

    /**
     * 默认左连接
     */
    Join join() default Join.LEFT;

    /**
     * 多字段模糊搜索，仅支持String类型字段，多个用逗号隔开, 如@Query(blurry = "email,username")
     */
    String blurry() default "";

    enum Type {
        // 相等
        EQUAL,
        // 不相等
        NOT_EQUAL,
        // 大于等于
        GREATER_THAN,
        // 小于等于
        LESS_THAN,
        // 中模糊查询
        INNER_LIKE,
        // 左模糊查询
        LEFT_LIKE,
        // 右模糊查询
        RIGHT_LIKE,
        // 小于 not equal
        LESS_THAN_NQ,
        // 包含
        IN,
        // 不包含
        NOT_IN,
        // 之间
        BETWEEN,
        // 为空
        IS_NULL,
        // 不为空
        NOT_NULL
    }

    /**
     * 简单连接查询
     */
    enum Join {
        LEFT,
        RIGHT,
        INNER
    }
}
