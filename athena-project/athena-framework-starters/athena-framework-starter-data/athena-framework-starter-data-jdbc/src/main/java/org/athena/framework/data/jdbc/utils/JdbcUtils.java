package org.athena.framework.data.jdbc.utils;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.apache.commons.lang3.StringUtils;
import org.arthena.framework.common.exception.TodoException;
import org.athena.framework.data.jdbc.create.bean.ClassTableInfo;
import org.athena.framework.data.jdbc.type.DbType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

/**
 * @author zhouzhitong
 * @since 2025/7/13
 **/
public class JdbcUtils {

    private static final Logger log = LoggerFactory.getLogger(JdbcUtils.class);

    /**
     * 判断是否是基础类型
     *
     * @param clazz 类
     * @return 是否是基础类型
     */
    public static boolean isBaseType(Class<?> clazz) {
        return clazz.isPrimitive() || String.class.isAssignableFrom(clazz)
                || Number.class.isAssignableFrom(clazz) || Boolean.class.isAssignableFrom(clazz);
    }

    /**
     * 判断是否是日期类型
     *
     * @param clazz 类
     * @return 是否是日期类型
     */
    public static boolean isDateType(Class<?> clazz) {
        return clazz.isAssignableFrom(java.util.Date.class)
                || clazz.isAssignableFrom(LocalDateTime.class) || clazz.isAssignableFrom(LocalDate.class);
    }

    public static ClassTableInfo buildClassTableInfo(Class<?> clazz) {
        Table table = clazz.getAnnotation(Table.class);
        List<Field> columns = new LinkedList<>();
        Class<?> tempClazz = clazz;
        while (tempClazz != Object.class) {
            Field[] allField = tempClazz.getDeclaredFields();
            for (Field field : allField) {
                if (field.isSynthetic() || field.getName().contains("serialVersionUID")) {
                    continue;
                }
                columns.add(field);
            }
            tempClazz = tempClazz.getSuperclass();
        }


        return ClassTableInfo.builder()
                .clazz(clazz)
                .table(table)
                .columns(columns)
                .build();
    }

    public static boolean isUnique(Field field) {
        Column column;
        if ((column = field.getAnnotation(Column.class)) != null) {
            return column.unique();
        }
        return false;
    }

    public static String getAddColumnDdl(String tableName, Field field, DbType dbType) {
        // alter table add column_name column_type default column_default comment column_comment
        // alter table `table_name` add `column_name` int default 'default_name' not null comment 'comment
        Column column = field.getAnnotation(Column.class);
        StringBuilder sb = new StringBuilder();
        if (column == null) {
            throw new TodoException();
        }
        String columnName = CamelCaseUtils.toSnakeCase(column.name());
        tableName = CamelCaseUtils.toSnakeCase(tableName);
        sb.append("ALTER TABLE `").append(tableName).append("`");
        sb.append(" ADD `").append(columnName).append("` ").append(dbType.getType(field.getType(), column.length()));
        String columnDefinition = column.columnDefinition();
        if (!column.nullable()) {
            sb.append(" NOT NULL");
        }
        if (StringUtils.isNotBlank(columnDefinition)) {
            sb.append(" ").append(columnDefinition);
        }


        return sb.toString();
    }

    public static String getColumnDdl(Field field, DbType dbType) {
        Column column = field.getAnnotation(Column.class);
        StringBuilder sb = new StringBuilder();
        if (column == null) {
            log.error("field:{} no @Column annotation", field.getName());
            throw new TodoException();
        }
        if (StringUtils.isNotBlank(column.name())) {
            sb.append(column.name());
        } else {
            sb.append(CamelCaseUtils.toSnakeCase(field.getName()));
        }
        sb.append(" ");

        String type = dbType.getType(field.getType(), column.length());
        sb.append(type).append(" ");

        Id id = field.getAnnotation(Id.class);
        if (id != null) {
            GeneratedValue generatedValue = field.getAnnotation(GeneratedValue.class);
            if (generatedValue != null) {
                if (generatedValue.strategy() == GenerationType.AUTO) {
                    sb.append(" AUTO_INCREMENT ");
                }
            }
            sb.append(" PRIMARY KEY ");
        }

        if (!column.nullable()) {
            sb.append(" NOT NULL ");
        }
        if (StringUtils.isNotBlank(column.columnDefinition())) {
            sb.append("comment ").append(column.columnDefinition()).append(" ");
        }


        return sb.toString();
    }

}
