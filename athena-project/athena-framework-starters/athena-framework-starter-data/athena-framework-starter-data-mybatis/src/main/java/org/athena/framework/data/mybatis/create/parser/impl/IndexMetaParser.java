package org.athena.framework.data.mybatis.create.parser.impl;

import com.baomidou.mybatisplus.annotation.TableId;
import jakarta.persistence.Id;
import org.apache.commons.lang3.StringUtils;
import org.arthena.framework.common.utils.CamelCaseUtils;
import org.athena.framework.data.jdbc.annotations.JdbcColumn;
import org.athena.framework.data.jdbc.annotations.JdbcIndex;
import org.athena.framework.data.jdbc.annotations.JdbcIndexType;
import org.athena.framework.data.mybatis.bean.TableMeta;
import org.athena.framework.data.mybatis.bean.meta.IndexMeta;
import org.athena.framework.data.mybatis.create.parser.ITableMetaParser;
import org.athena.framework.data.mybatis.utils.TableFieldParseUtils;
import org.springframework.core.Ordered;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class IndexMetaParser implements ITableMetaParser {

    @Override
    public boolean parse(Class<?> clazz, TableMeta tableMeta) {
        List<IndexMeta> indexMetas = parseIndexMeta(clazz, tableMeta);
        tableMeta.setIndexes(indexMetas);
        return true;
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    protected List<IndexMeta> parseIndexMeta(Class<?> clazz, TableMeta tableMeta) {

        List<IndexMeta> indexMetas = new ArrayList<>();
        Class<?> tempClazz = clazz;

        while (tempClazz != Object.class) {
            Field[] allField = tempClazz.getDeclaredFields();
            for (Field field : allField) {
                if (field.isSynthetic() || field.getName().contains("serialVersionUID")) {
                    continue;
                }
                Id id = field.getAnnotation(Id.class);
                TableId tableId = field.getAnnotation(TableId.class);
                if (id != null || tableId != null || TableFieldParseUtils.isPrimaryKey(field)) {
                    indexMetas.add(IndexMeta.builder()
                            .type("PRIMARY")
                            .columnNames(List.of(TableFieldParseUtils.getColumnName(field)))
                            .build());
                    continue;
                }
                JdbcColumn jdbcColumn = field.getAnnotation(JdbcColumn.class);
                if (jdbcColumn != null && jdbcColumn.unique()) {
                    String columnName = TableFieldParseUtils.getColumnName(field);
                    List<String> columnNames = List.of(columnName);
                    indexMetas.add(IndexMeta.builder()
                            .name(getDefaultIndexName(clazz, tableMeta, JdbcIndexType.UNIQUE, columnNames))
                            .type("INDEX")
                            .columnNames(columnNames)
                            .unique(true)
                            .build());
                }
            }
            tempClazz = tempClazz.getSuperclass();
        }
        addJdbcIndexes(clazz, tableMeta, indexMetas);
        return indexMetas;
    }

    private void addJdbcIndexes(Class<?> clazz, TableMeta tableMeta, List<IndexMeta> indexMetas) {
        JdbcIndex[] jdbcIndexes = clazz.getAnnotationsByType(JdbcIndex.class);
        for (JdbcIndex jdbcIndex : jdbcIndexes) {
            List<String> columnNames = Arrays.stream(jdbcIndex.columnNames())
                    .filter(StringUtils::isNotBlank)
                    .collect(Collectors.toList());
            if (columnNames.isEmpty()) {
                throw new IllegalArgumentException("JdbcIndex columnNames must not be empty: " + clazz.getName());
            }

            JdbcIndexType indexType = jdbcIndex.type();
            indexMetas.add(IndexMeta.builder()
                    .name(getIndexName(clazz, jdbcIndex, tableMeta, columnNames))
                    .type(getIndexMetaType(indexType))
                    .columnNames(columnNames)
                    .unique(JdbcIndexType.UNIQUE == indexType)
                    .build());
        }
    }

    private String getIndexName(Class<?> clazz, JdbcIndex jdbcIndex, TableMeta tableMeta, List<String> columnNames) {
        if (StringUtils.isNotBlank(jdbcIndex.name())) {
            return jdbcIndex.name();
        }
        return getDefaultIndexName(clazz, tableMeta, jdbcIndex.type(), columnNames);
    }

    private String getDefaultIndexName(Class<?> clazz, TableMeta tableMeta, JdbcIndexType indexType, List<String> columnNames) {
        String tableName = tableMeta == null ? null : tableMeta.getName();
        if (StringUtils.isBlank(tableName)) {
            tableName = CamelCaseUtils.toSnakeCase(clazz.getSimpleName());
        }
        return tableName + "_" + indexType.getCode() + "_" + String.join("_", columnNames);
    }

    private String getIndexMetaType(JdbcIndexType indexType) {
        return switch (indexType) {
            case UNIQUE, INDEX -> "INDEX";
            case FULLTEXT -> "FULLTEXT INDEX";
            case SPATIAL -> "SPATIAL INDEX";
        };
    }

}
