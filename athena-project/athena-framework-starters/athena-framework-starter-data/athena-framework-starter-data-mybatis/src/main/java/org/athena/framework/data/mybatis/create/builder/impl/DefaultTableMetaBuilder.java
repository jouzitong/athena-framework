package org.athena.framework.data.mybatis.create.builder.impl;

import org.athena.framework.data.mybatis.bean.TableMeta;
import org.athena.framework.data.mybatis.create.builder.ITableMetaBuilder;
import org.athena.framework.data.mybatis.create.parser.ITableMetaParser;
import org.athena.framework.data.mybatis.create.parser.impl.DefaultTableMetaParser;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 默认的表元数据构建器实现类。该类实现了ITableMetaBuilder接口，用于根据给定的类和解析器来生成表元数据。
 * <p>
 * 构建过程包括设置需要转换为表元数据的类、添加解析器以及执行构建操作以生成最终的TableMeta对象。
 * 在构建过程中，如果指定的类为空，则会抛出异常；同时，该类还提供了初始化解析器的方法，确保至少有一个默认解析器被添加到解析器列表中。
 */
public class DefaultTableMetaBuilder implements ITableMetaBuilder {

    private Class<?> clazz;
    private List<ITableMetaParser> parsers = new ArrayList<>();

    @Override
    public ITableMetaBuilder clazz(Class<?> clazz) {
        this.clazz = clazz;
        return this;
    }

    @Override
    public ITableMetaBuilder addParser(ITableMetaParser... parsers) {
        if (parsers == null) {
            return this;
        }
        for (ITableMetaParser parser : parsers) {
            // 排除掉 DefaultTableMetaParser 解析器
            if (parser.getClass() == DefaultTableMetaParser.class) {
                continue;
            }
            this.parsers.add(parser);
        }
        return this;
    }

    @Override
    public TableMeta build() {
        if (clazz == null) {
            throw new IllegalStateException("Class must be set before building TableMeta");
        }
        initParser();
        TableMeta tableMeta = new TableMeta();
        for (ITableMetaParser parser : parsers) {
            if (!parser.parse(clazz, tableMeta)) {
                // 停止解析
                break;
            }
        }
        return tableMeta;
    }

    /**
     * 初始化解析器, 主要是把默认的解析器添加进去
     */
    protected void initParser() {
        this.parsers.add(new DefaultTableMetaParser());
        // 排序下 从小到大排序
        parsers.sort(Comparator.comparingInt(ITableMetaParser::getOrder));
    }

}
