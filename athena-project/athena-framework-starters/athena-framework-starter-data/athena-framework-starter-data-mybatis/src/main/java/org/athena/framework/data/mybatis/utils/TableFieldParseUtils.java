package org.athena.framework.data.mybatis.utils;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.apache.commons.lang3.StringUtils;
import org.arthena.framework.common.utils.CamelCaseUtils;
import org.athena.framework.data.mybatis.annotations.DdlColumnLength;
import org.athena.framework.data.mybatis.bean.meta.ColumnMeta;

import java.lang.reflect.Field;

/**
 * 提供了用于解析表字段的工具方法。
 */
public class TableFieldParseUtils {

    private static final int DEFAULT_STRING_LENGTH = 255;

    /**
     * 解析给定的字段并返回其对应的列元数据
     *
     * @param field 要解析的字段
     * @return 包含字段信息的 ColumnMeta 对象
     */
    public static ColumnMeta parseField(Field field) {
        Column column = field.getAnnotation(Column.class);
        TableField tableField = field.getAnnotation(TableField.class);
        TableId tableId = field.getAnnotation(TableId.class);
        if (!isPersistentField(field)) {
            throw new IllegalArgumentException("Field is missing persistence column annotation");
        }

        String name = getColumnName(field);
        Class<?> javaType = field.getType();
        String dataType = column != null && StringUtils.isNotBlank(column.columnDefinition())
                ? column.columnDefinition()
                : null;
        int length = getLength(field, column);
        Integer scale = column == null ? null : column.scale();
        boolean primaryKey = field.isAnnotationPresent(Id.class) || tableId != null;
        boolean nullable = column == null ? !primaryKey : column.nullable();
        boolean autoIncrement = false;
        Id id = field.getAnnotation(Id.class);
        if (id != null) {
            GeneratedValue generatedValue = field.getAnnotation(GeneratedValue.class);
            if (generatedValue != null && generatedValue.strategy() == GenerationType.AUTO) {
                autoIncrement = true;
            }
        }
        if (tableId != null && tableId.type() == IdType.AUTO) {
            autoIncrement = true;
        }

        return ColumnMeta.builder()
                .name(name)
                .dataType(dataType)
                .javaType(javaType)
                .length(length)
                .scale(scale)
                .nullable(nullable)
                .primaryKey(primaryKey)
                .autoIncrement(autoIncrement)
                .defaultValue(null)
                .comment(null)
                .build();
    }

    public static boolean isPersistentField(Field field) {
        TableField tableField = field.getAnnotation(TableField.class);
        if (tableField != null && !tableField.exist()) {
            return false;
        }
        return field.isAnnotationPresent(Column.class)
                || tableField != null
                || field.isAnnotationPresent(TableId.class)
                || field.isAnnotationPresent(Id.class);
    }

    public static String getColumnName(Field field) {
        Column column = field.getAnnotation(Column.class);
        if (column != null && StringUtils.isNotBlank(column.name())) {
            return column.name();
        }
        TableField tableField = field.getAnnotation(TableField.class);
        if (tableField != null && StringUtils.isNotBlank(tableField.value())) {
            return tableField.value();
        }
        TableId tableId = field.getAnnotation(TableId.class);
        if (tableId != null && StringUtils.isNotBlank(tableId.value())) {
            return tableId.value();
        }
        return CamelCaseUtils.toSnakeCase(field.getName());
    }

    private static int getLength(Field field, Column column) {
        DdlColumnLength ddlColumnLength = field.getAnnotation(DdlColumnLength.class);
        if (ddlColumnLength != null && ddlColumnLength.value() > 0) {
            return ddlColumnLength.value();
        }
        if (column != null && column.length() > 0) {
            return column.length();
        }
        if (String.class.equals(field.getType())) {
            return DEFAULT_STRING_LENGTH;
        }
        return 0;
    }

}
