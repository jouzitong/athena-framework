package org.arthena.framework.common.annotation;

import org.arthena.framework.common.base.IEnum;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 所有枚举都要添加的注解
 * 与 {@link IEnum} 配套使用
 * @author zhouzhitong
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface GlobalEnum {

    /**
     * 指定全局枚举 key 值
     *
     * @return name
     */
    String name() default "";

}
