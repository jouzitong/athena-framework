package org.arthena.framework.common.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author zhouzhitong
 * @since 2025/8/6
 **/
public class ObjectUtils {

    /**
     * 判断是否是基础类型
     *
     * @param clazz 类
     * @return 是否是基础类型
     */
    public static boolean isBaseType(Class<?> clazz) {
        return clazz.isPrimitive() || String.class.isAssignableFrom(clazz)
                || Number.class.isAssignableFrom(clazz) || Boolean.class.isAssignableFrom(clazz);
    }

    /**
     * 判断是否是日期类型
     *
     * @param clazz 类
     * @return 是否是日期类型
     */
    public static boolean isDateType(Class<?> clazz) {
        return clazz.isAssignableFrom(java.util.Date.class)
                || clazz.isAssignableFrom(LocalDateTime.class) || clazz.isAssignableFrom(LocalDate.class);
    }


}
