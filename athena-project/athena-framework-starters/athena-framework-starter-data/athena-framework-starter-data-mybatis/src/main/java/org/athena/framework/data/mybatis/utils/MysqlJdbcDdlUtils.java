package org.athena.framework.data.mybatis.utils;

import org.apache.commons.lang3.StringUtils;
import org.athena.framework.data.jdbc.type.DbType;
import org.athena.framework.data.mybatis.bean.TableMeta;
import org.athena.framework.data.mybatis.bean.meta.ColumnMeta;
import org.athena.framework.data.mybatis.bean.meta.IndexMeta;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class MysqlJdbcDdlUtils {

    private static final String COMMENT_SYMBOL = "--";

    /**
     * 生成 创建表的 DDL SQL
     *
     * @param tableMeta 表定义
     * @return DDL SQL
     */
    public static String genCreateDdlSql(TableMeta tableMeta) {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE IF NOT EXISTS ").append(quoteIdentifier(tableMeta.getName())).append(" (\n");

        // 列定义
        List<String> columnDefs = tableMeta.getColumns().stream()
                .map(column -> buildColumnDefinition(column, DbType.MYSQL))
                .collect(Collectors.toList());
        sb.append(String.join(",\n", columnDefs));

        // 主键定义
        if (tableMeta.getIndexes() != null && !tableMeta.getIndexes().isEmpty()) {
            for (IndexMeta index : tableMeta.getIndexes()) {
                if ("PRIMARY".equalsIgnoreCase(index.getType())) {
                    sb.append(",\n  PRIMARY KEY (").append(joinColumnNames(index.getColumnNames())).append(")");
                    break; // 假设只有一个主键
                }
            }
        }

        // 其他索引定义
        if (tableMeta.getIndexes() != null && !tableMeta.getIndexes().isEmpty()) {
            for (IndexMeta index : tableMeta.getIndexes()) {
                if (!"PRIMARY".equalsIgnoreCase(index.getType())) {
                    String indexType = index.isUnique() ? "UNIQUE INDEX" : index.getType();
                    sb.append(",\n  ").append(indexType).append(" ").append(quoteIdentifier(index.getName()))
                            .append(" (").append(joinColumnNames(index.getColumnNames()))
                            .append(")");
                }
            }
        }

        sb.append("\n);");

        return sb.toString();
    }


    /**
     * 生成 修改表的 DDL SQL
     *
     * @param newTableMeta 新表定义
     * @param oldTableMeta 旧表定义
     * @return DDL SQL
     */
    public static String genUpdateDdlSql(TableMeta newTableMeta, TableMeta oldTableMeta) {
        return genUpdateDdlSql(newTableMeta, oldTableMeta, true, false, false);
    }

    public static String genUpdateDdlSql(TableMeta newTableMeta,
                                         TableMeta oldTableMeta,
                                         boolean autoAddColumn,
                                         boolean autoUpdateColumn,
                                         boolean autoDropColumn) {
        if (newTableMeta == null || oldTableMeta == null || newTableMeta.equals(oldTableMeta)) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("ALTER TABLE ").append(quoteIdentifier(newTableMeta.getName())).append("\n");
        int beforeLength = sb.length();

        if (autoAddColumn) {
            addNewColumns(sb, newTableMeta, oldTableMeta);
        }

        if (autoUpdateColumn) {
            updateChangedColumns(sb, newTableMeta, oldTableMeta);
        }

        if (autoDropColumn) {
            dropRemovedColumns(sb, newTableMeta, oldTableMeta);
        }

        if (sb.length() == beforeLength) {
            return null;
        }

        removeTrailingComma(sb);

        sb.append(";");

        return sb.toString();
    }

    private static void removeTrailingComma(StringBuilder sb) {
        int index = sb.length() - 1;
        while (index >= 0 && Character.isWhitespace(sb.charAt(index))) {
            index--;
        }
        if (index >= 0 && sb.charAt(index) == ',') {
            sb.delete(index, sb.length());
            sb.append("\n");
        }
    }

    private static void addNewColumns(StringBuilder sb, TableMeta newTableMeta, TableMeta oldTableMeta) {
        List<ColumnMeta> newColumns = newTableMeta.getColumns().stream()
                .filter(column -> findColumn(oldTableMeta, column.getName()) == null)
                .collect(Collectors.toList());
        if (!newColumns.isEmpty()) {
            sb.append(COMMENT_SYMBOL).append(" 新增字段\n");
            for (ColumnMeta column : newColumns) {
                sb.append("ADD COLUMN ").append(buildColumnDefinition(column, DbType.MYSQL).trim()).append(",\n");
            }
        }
    }

    private static void dropRemovedColumns(StringBuilder sb, TableMeta newTableMeta, TableMeta oldTableMeta) {
        List<ColumnMeta> removedColumns = oldTableMeta.getColumns().stream()
                .filter(column -> findColumn(newTableMeta, column.getName()) == null)
                .collect(Collectors.toList());
        if (!removedColumns.isEmpty()) {
            sb.append(COMMENT_SYMBOL).append(" 删除字段\n");
            for (ColumnMeta column : removedColumns) {
                sb.append("DROP COLUMN ").append(quoteIdentifier(column.getName())).append(",\n");
            }
        }
    }

    /**
     * 生成已存在字段的定义变更语句。
     * <p>
     * MySQL 的 {@code MODIFY COLUMN} 需要携带完整字段定义，因此这里始终使用新实体的字段定义生成 SQL。
     */
    private static void updateChangedColumns(StringBuilder sb, TableMeta newTableMeta, TableMeta oldTableMeta) {
        List<ColumnMeta> changedColumns = newTableMeta.getColumns().stream()
                .filter(column -> {
                    ColumnMeta oldColumn = findColumn(oldTableMeta, column.getName());
                    return oldColumn != null && isColumnDefinitionChanged(column, oldColumn);
                })
                .collect(Collectors.toList());
        if (!changedColumns.isEmpty()) {
            sb.append(COMMENT_SYMBOL).append(" 更新字段定义\n");
            for (ColumnMeta column : changedColumns) {
                sb.append(buildColumnAlterDefinition(column, DbType.MYSQL).trim()).append(",\n");
            }
        }
    }

    private static boolean isColumnDefinitionChanged(ColumnMeta newColumn, ColumnMeta oldColumn) {
        return !isColumnTypeEqual(newColumn, oldColumn)
                || newColumn.isNullable() != oldColumn.isNullable()
                || newColumn.isAutoIncrement() != oldColumn.isAutoIncrement()
                || !isDefaultValueEqual(newColumn, oldColumn)
                || !StringUtils.equals(normalizeValue(newColumn.getComment()), normalizeValue(oldColumn.getComment()));
    }

    private static boolean isColumnTypeEqual(ColumnMeta newColumn, ColumnMeta oldColumn) {
        String expectedType = resolveColumnType(newColumn, DbType.MYSQL);
        String actualType = resolveColumnType(oldColumn, DbType.MYSQL);
        String expectedTypeName = getTypeName(expectedType);
        String actualTypeName = getTypeName(actualType);
        if (isBooleanTinyintEquivalent(expectedTypeName, actualTypeName)) {
            return true;
        }
        if (!StringUtils.equalsIgnoreCase(expectedTypeName, actualTypeName)) {
            return false;
        }

        // MySQL 非字符串字段的显示长度不影响实际存储范围，例如 BIGINT(20) 与 BIGINT。
        if (!isStringType(expectedTypeName)) {
            return true;
        }

        List<Integer> expectedArguments = getTypeArguments(expectedType);
        if (expectedArguments.isEmpty()) {
            return true;
        }
        List<Integer> actualArguments = getTypeArguments(actualType);
        if (actualArguments.isEmpty()) {
            actualArguments = getColumnTypeArguments(oldColumn, expectedArguments.size());
        }
        return expectedArguments.equals(actualArguments);
    }

    private static boolean isDefaultValueEqual(ColumnMeta newColumn, ColumnMeta oldColumn) {
        String expectedDefaultValue = normalizeValue(newColumn.getDefaultValue());
        String actualDefaultValue = normalizeValue(oldColumn.getDefaultValue());
        if (StringUtils.equalsIgnoreCase(expectedDefaultValue, actualDefaultValue)) {
            return true;
        }
        return isBooleanTinyintEquivalent(
                getTypeName(resolveColumnType(newColumn, DbType.MYSQL)),
                getTypeName(resolveColumnType(oldColumn, DbType.MYSQL))
        ) && isBooleanDefaultValue(expectedDefaultValue) && isBooleanDefaultValue(actualDefaultValue)
                && toBooleanDefaultValue(expectedDefaultValue) == toBooleanDefaultValue(actualDefaultValue);
    }

    /**
     * MySQL JDBC 元数据不会稳定保留 TINYINT 的显示宽度，因此按 BOOLEAN 与 TINYINT 等价处理。
     */
    private static boolean isBooleanTinyintEquivalent(String expectedTypeName, String actualTypeName) {
        return ("BOOLEAN".equalsIgnoreCase(expectedTypeName) && "TINYINT".equalsIgnoreCase(actualTypeName))
                || ("TINYINT".equalsIgnoreCase(expectedTypeName) && "BOOLEAN".equalsIgnoreCase(actualTypeName));
    }

    private static boolean isStringType(String typeName) {
        return "CHAR".equalsIgnoreCase(typeName) || "VARCHAR".equalsIgnoreCase(typeName);
    }

    private static boolean isBooleanDefaultValue(String value) {
        return "TRUE".equalsIgnoreCase(value)
                || "FALSE".equalsIgnoreCase(value)
                || "1".equals(value)
                || "0".equals(value);
    }

    private static boolean toBooleanDefaultValue(String value) {
        return "TRUE".equalsIgnoreCase(value) || "1".equals(value);
    }

    private static String getTypeName(String type) {
        if (StringUtils.isBlank(type)) {
            return StringUtils.EMPTY;
        }
        int argumentStart = type.indexOf('(');
        return (argumentStart < 0 ? type : type.substring(0, argumentStart)).trim().replaceAll("\\s+", " ");
    }

    private static List<Integer> getTypeArguments(String type) {
        if (StringUtils.isBlank(type)) {
            return Collections.emptyList();
        }
        int start = type.indexOf('(');
        int end = type.lastIndexOf(')');
        if (start < 0 || end <= start) {
            return Collections.emptyList();
        }
        try {
            return Arrays.stream(type.substring(start + 1, end).split(","))
                    .map(String::trim)
                    .map(Integer::valueOf)
                    .collect(Collectors.toList());
        } catch (NumberFormatException ex) {
            return Collections.emptyList();
        }
    }

    private static List<Integer> getColumnTypeArguments(ColumnMeta column, int argumentCount) {
        if (column.getLength() <= 0) {
            return Collections.emptyList();
        }
        if (argumentCount > 1) {
            return Arrays.asList(column.getLength(), column.getScale());
        }
        return Collections.singletonList(column.getLength());
    }

    private static String normalizeValue(String value) {
        return StringUtils.isBlank(value) ? null : value.trim();
    }

    private static String buildColumnDefinition(ColumnMeta column, DbType dbType) {
        StringBuilder columnDef = new StringBuilder("  ");
        columnDef
                .append(quoteIdentifier(column.getName())).append(" ")
                .append(resolveColumnType(column, dbType));

        if (column.isNullable()) {
            columnDef.append(" NULL");
        } else {
            columnDef.append(" NOT NULL");
        }

        if (column.isAutoIncrement()) {
            columnDef.append(" AUTO_INCREMENT");
        }

        if (column.getDefaultValue() != null) {
            columnDef.append(" DEFAULT ").append(column.getDefaultValue());
        }
        // comment
        if (StringUtils.isNotBlank(column.getComment())) {
            columnDef.append(" COMMENT '").append(column.getComment()).append("'");
        }

        return columnDef.toString();
    }

    private static ColumnMeta findColumn(TableMeta tableMeta, String columnName) {
        if (tableMeta == null || tableMeta.getColumns() == null || StringUtils.isBlank(columnName)) {
            return null;
        }
        String normalizedColumnName = normalizeIdentifier(columnName);
        return tableMeta.getColumns().stream()
                .filter(column -> normalizedColumnName.equalsIgnoreCase(normalizeIdentifier(column.getName())))
                .findFirst()
                .orElse(null);
    }

    private static String buildColumnAlterDefinition(ColumnMeta column, DbType dbType) {
        StringBuilder columnDef = new StringBuilder("  MODIFY COLUMN ");
        columnDef.append(quoteIdentifier(column.getName())).append(" ").append(resolveColumnType(column, dbType));

        if (column.isNullable()) {
            columnDef.append(" NULL");
        } else {
            columnDef.append(" NOT NULL");
        }

        if (column.isAutoIncrement()) {
            columnDef.append(" AUTO_INCREMENT");
        }

        if (column.getDefaultValue() != null) {
            columnDef.append(" DEFAULT ").append(column.getDefaultValue());
        }
        // comment
        if (StringUtils.isNotBlank(column.getComment())) {
            columnDef.append(" COMMENT '").append(column.getComment()).append("'");
        }

        return columnDef.toString();
    }

    private static String resolveColumnType(ColumnMeta column, DbType dbType) {
        if (StringUtils.isNotBlank(column.getDataType())) {
            return column.getDataType();
        }
        return dbType.getType(column.getJavaType(), column.getLength());
    }

    private static String buildIndexAlterDefinition(IndexMeta index, String action) {
        StringBuilder indexDef = new StringBuilder("  ").append(action).append(" INDEX ");
        indexDef.append(quoteIdentifier(index.getName())).append(" (");
        indexDef.append(joinColumnNames(index.getColumnNames()));
        indexDef.append(")");

        return indexDef.toString();
    }

    private static String joinColumnNames(List<String> columnNames) {
        return columnNames.stream()
                .map(MysqlJdbcDdlUtils::quoteIdentifier)
                .collect(Collectors.joining(", "));
    }

    private static String quoteIdentifier(String identifier) {
        if (StringUtils.isBlank(identifier)) {
            return identifier;
        }
        String value = identifier.trim();
        if (value.startsWith("`") && value.endsWith("`")) {
            return value;
        }
        return "`" + value + "`";
    }

    private static String normalizeIdentifier(String identifier) {
        if (identifier == null) {
            return null;
        }
        String value = identifier.trim();
        if (value.startsWith("`") && value.endsWith("`") && value.length() > 1) {
            return value.substring(1, value.length() - 1);
        }
        return value;
    }

    public static void main(String[] args) {
        // 旧表定义
        TableMeta oldTableMeta = TableMeta.builder()
                .name("example_table")
                .comment("Old Example Table")
                .columns(Arrays.asList(
                        ColumnMeta.builder()
                                .name("id")
                                .javaType(Integer.class)
                                .nullable(false)
                                .build(),
                        ColumnMeta.builder()
                                .name("name")
                                .javaType(String.class)
                                .length(50)
                                .nullable(true)
                                .build(),
                        ColumnMeta.builder()
                                .name("created_at")
                                .javaType(java.util.Date.class)
                                .nullable(false)
                                .build()
                ))
                .indexes(Arrays.asList(
                        IndexMeta.builder()
                                .name("idx_name")
                                .columnNames(Arrays.asList("name"))
                                .type("INDEX")
                                .build()
                ))
                .build();

        // 新表定义
        TableMeta newTableMeta = TableMeta.builder()
                .name("example_table")
                .comment("New Example Table")
                .columns(Arrays.asList(
                        ColumnMeta.builder()
                                .name("id")
                                .javaType(Integer.class)
                                .nullable(false)
                                .build(),
                        ColumnMeta.builder()
                                .name("age")
                                .javaType(Integer.class)
                                .nullable(true)
                                .build(),
                        ColumnMeta.builder()
                                .name("email")
                                .javaType(String.class)
                                .length(100)
                                .nullable(true)
                                .build(),
                        ColumnMeta.builder()
                                .name("created_at")
                                .javaType(java.util.Date.class)
                                .nullable(false)
                                .build()
                ))
                .indexes(Arrays.asList(
                        IndexMeta.builder()
                                .name("idx_email")
                                .columnNames(Arrays.asList("email"))
                                .type("INDEX")
                                .build()
                ))
                .build();

        String createDdlSql = genCreateDdlSql(newTableMeta);
        System.out.println("创建表SQL : \n" + createDdlSql);

        // 生成 DDL SQL
        String ddlSql = MysqlJdbcDdlUtils.genUpdateDdlSql(newTableMeta, oldTableMeta);

        // 输出结果
        System.out.println("修改表SQL : \n" + ddlSql);
    }

}
