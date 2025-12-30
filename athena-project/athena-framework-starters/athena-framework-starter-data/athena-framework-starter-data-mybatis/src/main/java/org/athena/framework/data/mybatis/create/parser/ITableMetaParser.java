package org.athena.framework.data.mybatis.create.parser;

import org.athena.framework.data.mybatis.bean.TableMeta;
import org.springframework.core.Ordered;

/**
 * 表定义信息解析器
 */
public interface ITableMetaParser extends Ordered {

    /**
     * 解析表元数据。
     *
     * @param clazz     用于解析表元数据的类
     * @param tableMeta 表定义信息
     * @return 是否继续解析. true 继续解析, false 停止解析
     */
    boolean parse(Class<?> clazz, TableMeta tableMeta);

    /**
     * 解析器执行顺序. 越小越先执行
     *
     * @return 执行顺序
     */
    @Override
    default int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
