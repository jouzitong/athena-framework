package org.athena.framework.data.mybatis.create.parser.impl;

import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Table;
import org.apache.commons.lang3.StringUtils;
import org.athena.framework.data.mybatis.bean.TableMeta;
import org.athena.framework.data.mybatis.bean.meta.ColumnMeta;
import org.athena.framework.data.mybatis.create.parser.ITableMetaParser;
import org.athena.framework.data.mybatis.utils.TableFieldParseUtils;
import org.springframework.core.Ordered;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 *
 */
public class DefaultTableMetaParser implements ITableMetaParser {

    @Override
    public boolean parse(Class<?> clazz, TableMeta tableMeta) {
        // 解析表定义信息
        parseTableInfo(clazz, tableMeta);
        // 解析字段定义信息
        List<ColumnMeta> columns = parseColumnInfo(clazz);
        tableMeta.setColumns(columns);
        // TODO 解析索引定义信息
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
            return parseColumnInfo(clazz);
        }
        return Collections.emptyList();
    }


    /**
     * 解析给定类中的表信息，并填充到TableMeta对象中。
     *
     * @param clazz     用于解析表元数据的类
     * @param tableMeta 表定义信息，将被填充解析出的数据
     */
    private void parseTableInfo(Class<?> clazz, TableMeta tableMeta) {
        Table table = clazz.getAnnotation(Table.class);
        TableName mp_table = clazz.getAnnotation(TableName.class);
        String tableName = null;
        if (table != null) {
            tableName = table.name();
        }
        if (mp_table != null) {
            tableName = mp_table.value();
        }
        if (StringUtils.isNotBlank(tableName)) {
            tableMeta.setName(tableName);
        }
    }

}
