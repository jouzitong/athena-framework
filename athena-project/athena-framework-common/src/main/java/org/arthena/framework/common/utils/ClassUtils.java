package org.arthena.framework.common.utils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * @author zhouzhitong
 * @since 2025/7/13
 **/
public class ClassUtils {

    public static List<Field> getAllFields(Class<?> c) {
        List<Field> allFields = new LinkedList<>();
        while (c != Object.class) {
            Field[] fields = c.getDeclaredFields();
            // 过滤掉可能的代理字段, 静态字段
            allFields.addAll(Arrays.stream(fields)
                    .filter(field -> !field.isSynthetic())
                    .toList());
            c = c.getSuperclass();
        }
        return allFields;
    }


}
