package org.arthena.framework.common.utils;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;

import java.beans.PropertyDescriptor;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * 属性赋值工具类
 *
 * @author zhouzhitong
 * @since 2022/12/30
 */
@Deprecated
public class BeanUtils extends org.springframework.beans.BeanUtils {

    /**
     * 复制属性, 忽略源对象中值为null的字段
     *
     * @param source 源对象
     * @param target 目标对象
     * @throws BeansException 属性复制异常
     */
    public static void copy(Object source, Object target) throws BeansException {
        copyProperties(source, target, getNullField(source));
    }

    /**
     * 复制源对象的属性到目标对象，用于更新操作。
     * 该方法会复制源对象中所有字段（包括null值）到目标对象。
     *
     * @param source 源对象
     * @param target 目标对象
     * @throws BeansException 如果在复制属性过程中发生异常，则抛出此异常
     */
    public static void copyForUpdate(Object source, Object target) throws BeansException {
        copyProperties(source, target, getFields(source));
    }

    private static String[] getFields(Object source) {
        BeanWrapper beanWrapper = new BeanWrapperImpl(source);
        PropertyDescriptor[] propertyDescriptors = beanWrapper.getPropertyDescriptors();
        Set<String> fieldSet = new HashSet<>();
        if (propertyDescriptors.length > 0) {
            for (PropertyDescriptor p : propertyDescriptors) {
                String name = p.getName();
                fieldSet.add(name);
            }
        }
        String[] fields = new String[fieldSet.size()];
        fieldSet.toArray(fields);
        return fields;
    }

    /**
     * 获取属性中为空的字段
     *
     * @param source 源对象
     * @return
     */
    private static String[] getNullField(Object source) {
        BeanWrapper beanWrapper = new BeanWrapperImpl(source);
        PropertyDescriptor[] propertyDescriptors = beanWrapper.getPropertyDescriptors();
        Set<String> nullFieldSet = new HashSet<>();
        if (propertyDescriptors.length > 0) {
            for (PropertyDescriptor p : propertyDescriptors) {
                String name = p.getName();
                Object value = beanWrapper.getPropertyValue(name);
                if (Objects.isNull(value)) {
                    nullFieldSet.add(name);
                }
            }
        }
        String[] nullField = new String[nullFieldSet.size()];
        nullFieldSet.toArray(nullField);
        return nullField;
    }

}
