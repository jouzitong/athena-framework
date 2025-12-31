package org.athena.framework.data.mybatis.create.parser.impl;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import org.athena.framework.data.mybatis.bean.TableMeta;
import org.athena.framework.data.mybatis.bean.meta.ColumnMeta;
import org.athena.framework.data.mybatis.create.parser.ITableMetaParser;
import org.athena.framework.data.mybatis.utils.EmbeddedPrefixUtils;
import org.athena.framework.data.mybatis.utils.TableFieldParseUtils;
import org.springframework.core.Ordered;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 *
 */
public class ColumnMetaParser implements ITableMetaParser {

    @Override
    public boolean parse(Class<?> clazz, TableMeta tableMeta) {
        // 解析字段定义信息
        List<ColumnMeta> columns = parseColumnInfo(clazz);
        tableMeta.setColumns(columns);
        return true;
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    protected List<ColumnMeta> parseColumnInfo(Class<?> clazz) {
        List<ColumnMeta> columns = new LinkedList<>();
        Class<?> tempClazz = clazz;
        while (tempClazz != Object.class) {
            Field[] allField = tempClazz.getDeclaredFields();
            for (Field field : allField) {
                if (field.isSynthetic() || field.getName().contains("serialVersionUID")) {
                    continue;
                }
                // 如果 添加了 @Embedded 注解, 则认为
                columns.addAll(parseField(field));
            }
            tempClazz = tempClazz.getSuperclass();
        }
        return columns;
    }

    /**
     * Extracts column metadata from field annotations
     */
    private List<ColumnMeta> parseField(Field field) {
        Column column = field.getAnnotation(Column.class);
        if (column != null) {
            return Collections.singletonList(TableFieldParseUtils.parseField(field));
        }
        Embedded embedded = field.getAnnotation(Embedded.class);
        if (embedded != null) {
            Class<?> clazz = field.getType();
            List<ColumnMeta> columns = parseColumnInfo(clazz);
            EmbeddedPrefixUtils.paddingPrefix(field, columns);
            return columns;
        }
        return Collections.emptyList();
    }

}
