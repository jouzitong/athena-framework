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
            allFields.addAll(Arrays.asList(fields));
            c = c.getSuperclass();
        }
        return allFields;
    }


}
