package org.athena.framework.data.jdbc.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 统一描述实体类的数据库表定义。
 *
 * @author zhouzhitong
 * @since 2026/7/17
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface JdbcTable {

    /**
     * 表名，默认按类名驼峰转下划线推导。
     */
    String name() default "";

    /**
     * 表注释。
     */
    String comment() default "";
}
