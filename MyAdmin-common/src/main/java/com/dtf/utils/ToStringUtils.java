package com.dtf.utils;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.lang.reflect.Field;

/**
 * 0 *
 * 1 * @Author:  deng.tengfei
 * 2 * @email:  imdtf@qq.com
 * 3 * @Date:  2021/6/12 0:08
 */
public class ToStringUtils {
    public static String toMyString(Object object) {
        ToStringBuilder builder = new ToStringBuilder(object);
        Field[] fields = object.getClass().getDeclaredFields();
        try {
            for (Field field : fields) {
                field.setAccessible(true);
                builder.append(field.getName(), field.get(object)).append('\n');
            }
        } catch (Exception e) {
            builder.append("toString builder encounter an error: " + e.getMessage());
        }
        return builder.toString();
    }
}
