package org.athena.framework.data.jdbc.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 统一描述实体字段的数据库列定义。
 *
 * @author zhouzhitong
 * @since 2026/7/7
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface JdbcColumn {

    /**
     * 列名，默认按字段名驼峰转下划线推导。
     */
    String name() default "";

    /**
     * 显式数据库类型定义，例如 VARCHAR(64)、DECIMAL(10,2)。
     */
    String dataType() default "";

    /**
     * 字段长度；小于等于 0 时走框架默认推导。
     */
    int length() default -1;

    /**
     * 小数位数；小于 0 表示未显式指定。
     */
    int scale() default -1;

    /**
     * 是否允许为空。
     */
    boolean nullable() default true;

    /**
     * 是否唯一。
     */
    boolean unique() default false;

    /**
     * 默认值，直接按 SQL 片段写入，例如 CURRENT_TIMESTAMP、0、'INIT'。
     */
    String defaultValue() default "";

    /**
     * 字段注释。
     */
    String comment() default "";
}
