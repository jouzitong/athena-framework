package org.athena.framework.data.jdbc.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据库索引定义容器。
 *
 * @author zhouzhitong
 * @since 2026/7/17
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface JdbcIndexes {

    JdbcIndex[] value();
}
