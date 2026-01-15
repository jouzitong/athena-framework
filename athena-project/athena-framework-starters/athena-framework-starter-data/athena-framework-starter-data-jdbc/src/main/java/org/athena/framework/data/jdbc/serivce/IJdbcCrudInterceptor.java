package org.athena.framework.data.jdbc.serivce;

import org.athena.framework.data.jdbc.context.CrudContext;
import org.springframework.core.Ordered;

/**
 *
 * @author zhouzhitong
 * @since 2026/1/15
 */
public interface IJdbcCrudInterceptor extends Ordered {

    Class<?> entityType();

    /**
     * 在执行CRUD操作前进行检查。
     *
     * @param context 当前的CRUD操作上下文，包含如操作类型、实体类型、原始参数等信息
     * @return 如果检查通过返回true，否则返回false。默认实现总是返回true。
     */
    default boolean beforeCheck(CrudContext context) {
        return true;
    }

    /**
     * 在执行CRUD操作之前调用的方法。
     *
     * @param context 当前的CRUD操作上下文，包含了如操作类型、实体类型、原始参数等信息
     */
    default void before(CrudContext context) {
    }

    /**
     * 执行后
     */
    default void after(CrudContext context) {
    }

    /**
     * 执行异常
     */
    default void onError(CrudContext context) {
    }

}
