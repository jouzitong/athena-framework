package org.athena.framework.data.mybatis.utils;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.apache.commons.lang3.StringUtils;
import org.athena.framework.data.jdbc.utils.CamelCaseUtils;
import org.athena.framework.data.mybatis.bean.meta.ColumnMeta;

import java.lang.reflect.Field;

/**
 * 提供了用于解析表字段的工具方法。
 */
public class TableFieldParseUtils {

    /**
     * 解析给定的字段并返回其对应的列元数据
     *
     * @param field 要解析的字段
     * @return 包含字段信息的 ColumnMeta 对象
     */
    public static ColumnMeta parseField(Field field) {
        Column column = field.getAnnotation(Column.class);
        if (column == null) {
            throw new IllegalArgumentException("Field is missing @Column annotation");
        }

        String name = StringUtils.isNotBlank(column.name())
                ? column.name()
                : CamelCaseUtils.toSnakeCase(field.getName());
        Class<?> javaType = field.getType();
//        String dataType = DbType.MYSQL.getType(javaType);
        String dataType = null;
        int length = column.length();
        Integer scale = column.scale();
        boolean nullable = column.nullable();
        boolean primaryKey = field.isAnnotationPresent(Id.class);
        boolean autoIncrement = false;
        Id id = field.getAnnotation(Id.class);
        if (id != null) {
            GeneratedValue generatedValue = field.getAnnotation(GeneratedValue.class);
            if (generatedValue != null && generatedValue.strategy() == GenerationType.AUTO) {
                autoIncrement = true;
            }
        }
        String comment = column.columnDefinition();

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
                .comment(comment)
                .build();
    }

}
