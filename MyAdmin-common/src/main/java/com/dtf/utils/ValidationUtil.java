package com.dtf.utils;

import cn.hutool.core.util.ObjectUtil;
import com.dtf.exception.BadRequestException;
import org.hibernate.validator.internal.constraintvalidators.bv.EmailValidator;

public class ValidationUtil {
    public static void isNull(Object object, String entity, String parameter, Object value) {
        if (ObjectUtil.isNull(object)) {
            String msg = entity + " 不存在: " + parameter + " is " + value;
            throw new BadRequestException(msg);
        }
    }

    public static boolean isEmail(String email) {
        return new EmailValidator().isValid(email, null);
    }
}
