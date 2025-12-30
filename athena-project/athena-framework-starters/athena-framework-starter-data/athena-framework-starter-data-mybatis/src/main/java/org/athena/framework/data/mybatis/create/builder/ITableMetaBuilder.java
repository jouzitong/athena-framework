package org.athena.framework.data.mybatis.create.builder;

import org.athena.framework.data.mybatis.bean.TableMeta;
import org.athena.framework.data.mybatis.create.parser.ITableMetaParser;

/**
 * 表定义信息构造器
 */
public interface ITableMetaBuilder {

    /**
     * 根据给定的类构建表元数据。
     *
     * @param clazz 用于生成表元数据的类
     * @return this
     */
    ITableMetaBuilder clazz(Class<?> clazz);

    /**
     * 添加表元数据解析器
     *
     * @param parsers 解析器列表
     * @return this
     */
    ITableMetaBuilder addParser(ITableMetaParser... parsers);

    TableMeta build();

}
