package org.athena.framework.data.mybatis.utils;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.arthena.framework.common.exception.TodoException;
import org.athena.framework.data.jdbc.type.DbType;
import org.athena.framework.data.jdbc.utils.CamelCaseUtils;
import org.athena.framework.data.mybatis.create.bean.ClassTableInfo;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

/**
 * @author zhouzhitong
 * @since 2025/7/13
 **/
@Slf4j
public class JdbcUtils {

    /**
     * 构建ClassTableInfo
     *
     * @param clazz 类
     * @return ClassTableInfo
     */
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

    public static String getTableName(ClassTableInfo tableInfo, @Nullable String ignorePrefix, @Nullable String ignoreSubfix) {
        if (tableInfo.getTable() != null) {
            return packageName(tableInfo.getTable().name());
        }
        if (tableInfo.getMybatisPlusTable() != null) {
            return packageName(tableInfo.getMybatisPlusTable().value());
        }
        String tableName = tableInfo.getClazz().getSimpleName();

        if (StringUtils.isNotBlank(ignorePrefix)) {
            tableName = tableName.substring(ignorePrefix.length());
        }
        if (StringUtils.isNotBlank(ignoreSubfix)) {
            tableName = tableName.substring(0, tableName.length() - ignoreSubfix.length());
        }
        tableName = CamelCaseUtils.toSnakeCase(tableName);
        return packageName(tableName);
    }

    /**
     * 判断字段是否是唯一索引字段
     *
     * @param field 字段
     * @return 是否是唯一索引字段
     */
    public static boolean isUnique(Field field) {
        Column column;
        if ((column = field.getAnnotation(Column.class)) != null) {
            return column.unique();
        }
        return false;
    }

    /**
     * 生成新增表的DDL
     *
     * @param tableName 表名
     * @param field     字段
     * @param dbType    数据库类型
     * @return
     */
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

    /**
     * 生成创建字段 DDL
     *
     * @param field  字段
     * @param dbType 数据库类型
     * @return 字段 DDL
     */
    public static String getColumnDdl(Field field, DbType dbType) {
        Column column = field.getAnnotation(Column.class);
        StringBuilder sb = new StringBuilder();
        if (column == null) {
            LOGGER.error("field:{} no @Column annotation", field.getName());
            throw new TodoException();
        }
        String columnName = column.name();
        if (StringUtils.isBlank(columnName)) {
            columnName = "`" + CamelCaseUtils.toSnakeCase(field.getName()) + "`";
        } else {
            if (!columnName.trim().startsWith("`")) {
                columnName = "`" + columnName.trim() + "`";
            }
        }
        sb.append(columnName).append(" ");

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

    private static String packageName(String name) {
        if (StringUtils.isBlank(name)) {
            return "";
        }
        name = name.trim();
        if (name.startsWith("`")) {
            return name;
        } else {
            return "`" + name + "`";
        }
    }

}
