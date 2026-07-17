package org.athena.framework.data.jdbc.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 统一描述实体类的数据库索引定义。
 *
 * @author zhouzhitong
 * @since 2026/7/17
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(JdbcIndexes.class)
public @interface JdbcIndex {

    /**
     * 索引名称；为空时按 {tableName}_{typeCode}_{columnName1}_{columnName2} 规则生成。
     */
    String name() default "";

    /**
     * 索引关联的列名，使用数据库列名。
     */
    String[] columnNames();

    /**
     * 索引类型。
     */
    JdbcIndexType type() default JdbcIndexType.INDEX;
}
