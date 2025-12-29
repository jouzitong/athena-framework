package org.athena.framework.data.mybatis.utils;

import org.athena.framework.data.jdbc.type.DbType;
import org.athena.framework.data.mybatis.bean.TableMeta;
import org.athena.framework.data.mybatis.bean.meta.ColumnMeta;
import org.athena.framework.data.mybatis.bean.meta.IndexMeta;

import java.util.List;
import java.util.stream.Collectors;

public class JdbcDdlUtils {

    /**
     * 生成 创建表的 DDL SQL
     *
     * @param tableMeta 表定义
     * @return DDL SQL
     */
    public static String genCreateDdlSql(TableMeta tableMeta) {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE IF NOT EXISTS ").append(tableMeta.getName()).append(" (\n");

        // 列定义
        List<String> columnDefs = tableMeta.getColumns().stream()
                .map(column -> buildColumnDefinition(column, DbType.MYSQL))
                .collect(Collectors.toList());
        sb.append(String.join(",\n", columnDefs));

        // 主键定义
        if (tableMeta.getIndexes() != null && !tableMeta.getIndexes().isEmpty()) {
            for (IndexMeta index : tableMeta.getIndexes()) {
                if ("PRIMARY".equalsIgnoreCase(index.getType())) {
                    sb.append(",\n  PRIMARY KEY (").append(String.join(", ", index.getColumnNames())).append(")");
                    break; // 假设只有一个主键
                }
            }
        }

        // 其他索引定义
        if (tableMeta.getIndexes() != null && !tableMeta.getIndexes().isEmpty()) {
            for (IndexMeta index : tableMeta.getIndexes()) {
                if (!"PRIMARY".equalsIgnoreCase(index.getType())) {
                    sb.append(",\n  ").append(index.getType()).append(" ").append(index.getName())
                            .append(" (").append(String.join(", ", index.getColumnNames()))
                            .append(")").append(index.isUnique() ? " UNIQUE" : "");
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
        if (newTableMeta == null || oldTableMeta == null || newTableMeta.equals(oldTableMeta)) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("ALTER TABLE ").append(newTableMeta.getName()).append("\n");

        // Add new columns
        List<ColumnMeta> newColumns = newTableMeta.getColumns().stream()
                .filter(column -> !oldTableMeta.getColumns().contains(column))
                .collect(Collectors.toList());
        if (!newColumns.isEmpty()) {
            sb.append("ADD (").append(String.join(",\n", newColumns.stream()
                    .map(column -> buildColumnDefinition(column, DbType.MYSQL))
                    .collect(Collectors.toList()))).append("),\n");
        }

        // Modify existing columns
        List<ColumnMeta> modifiedColumns = newTableMeta.getColumns().stream()
                .filter(newColumn -> oldTableMeta.getColumns().stream()
                        .anyMatch(oldColumn -> !newColumn.equals(oldColumn) && newColumn.getName().equals(oldColumn.getName())))
                .collect(Collectors.toList());
        if (!modifiedColumns.isEmpty()) {
            for (ColumnMeta column : modifiedColumns) {
                sb.append(buildColumnAlterDefinition(column, DbType.MYSQL)).append(",\n");
            }
        }

        // Drop removed columns
        List<ColumnMeta> removedColumns = oldTableMeta.getColumns().stream()
                .filter(column -> !newTableMeta.getColumns().contains(column))
                .collect(Collectors.toList());
        if (!removedColumns.isEmpty()) {
            for (ColumnMeta column : removedColumns) {
                sb.append("DROP COLUMN `").append(column.getName()).append("`,\n");
            }
        }

        // Add new indexes
        List<IndexMeta> newIndexes = newTableMeta.getIndexes().stream()
                .filter(index -> !oldTableMeta.getIndexes().contains(index))
                .collect(Collectors.toList());
        if (!newIndexes.isEmpty()) {
            for (IndexMeta index : newIndexes) {
                sb.append(buildIndexAlterDefinition(index, "ADD")).append(",\n");
            }
        }

        // Modify existing indexes
        List<IndexMeta> modifiedIndexes = newTableMeta.getIndexes().stream()
                .filter(newIndex -> oldTableMeta.getIndexes().stream()
                        .anyMatch(oldIndex -> !newIndex.equals(oldIndex) && newIndex.getName().equals(oldIndex.getName())))
                .collect(Collectors.toList());
        if (!modifiedIndexes.isEmpty()) {
            for (IndexMeta index : modifiedIndexes) {
                sb.append(buildIndexAlterDefinition(index, "ALTER")).append(",\n");
            }
        }

        // Drop removed indexes
        List<IndexMeta> removedIndexes = oldTableMeta.getIndexes().stream()
                .filter(index -> !newTableMeta.getIndexes().contains(index))
                .collect(Collectors.toList());
        if (!removedIndexes.isEmpty()) {
            for (IndexMeta index : removedIndexes) {
                sb.append("DROP INDEX ").append(index.getName()).append(",\n");
            }
        }

        // Remove the last comma and newline
        if (sb.charAt(sb.length() - 1) == ',') {
            sb.setLength(sb.length() - 2);
        }

        sb.append(";");

        return sb.toString();
    }

    private static String buildColumnAlterDefinition(ColumnMeta column, DbType dbType) {
        StringBuilder columnDef = new StringBuilder("  MODIFY ");
        columnDef.append(column.getName()).append(" ").append(dbType.getType(column.getJavaType(), column.getLength()));

        if (column.isNullable()) {
            columnDef.append(" NULL");
        } else {
            columnDef.append(" NOT NULL");
        }

        if (column.getDefaultValue() != null) {
            columnDef.append(" DEFAULT ").append(column.getDefaultValue());
        }

        return columnDef.toString();
    }

    private static String buildIndexAlterDefinition(IndexMeta index, String operation) {
        StringBuilder indexDef = new StringBuilder("  ").append(operation).append(" INDEX ").append(index.getName())
                .append(" (").append(String.join(", ", index.getColumnNames()))
                .append(")").append(index.isUnique() ? " UNIQUE" : "");

        return indexDef.toString();
    }

    private static String buildColumnDefinition(ColumnMeta column, DbType dbType) {
        StringBuilder columnDef = new StringBuilder("  ");
        columnDef.append(column.getName()).append(" ").append(dbType.getType(column.getJavaType(), column.getLength()));

        if (column.isNullable()) {
            columnDef.append(" NULL");
        } else {
            columnDef.append(" NOT NULL");
        }

        if (column.getDefaultValue() != null) {
            columnDef.append(" DEFAULT ").append(column.getDefaultValue());
        }

        return columnDef.toString();
    }


    public static void main(String[] args) {
        // 创建示例 TableMeta 对象
        TableMeta tableMeta = TableMeta.builder()
                .name("example_table")
                .columns(List.of(
                        ColumnMeta.builder()
                                .name("id")
                                .javaType(Integer.class)
                                .length(11)
                                .nullable(false)
                                .defaultValue(null)
                                .build(),
                        ColumnMeta.builder()
                                .name("name")
                                .javaType(String.class)
                                .length(50)
                                .nullable(true)
                                .defaultValue("'default_name'")
                                .build()
                ))
                .indexes(List.of(
                        IndexMeta.builder()
                                .name("primary_key")
                                .type("PRIMARY")
                                .columnNames(List.of("id"))
                                .unique(true)
                                .build(),
                        IndexMeta.builder()
                                .name("idx_name")
                                .type("INDEX")
                                .columnNames(List.of("name"))
                                .unique(false)
                                .build()


                ))
                .build();

        // 生成创建表的 DDL SQL
        String createDdlSql = genCreateDdlSql(tableMeta);
        // 打印生成的 DDL SQL
        System.out.println(createDdlSql);

        // 创建新的 TableMeta 对象，新增两列，删除 name 列
        TableMeta newTableMeta = TableMeta.builder()
                .name("example_table")
                .columns(List.of(
                        ColumnMeta.builder()
                                .name("id")
                                .javaType(Integer.class)
                                .length(11)
                                .nullable(false)
                                .defaultValue(null)
                                .build(),
                        ColumnMeta.builder()
                                .name("age")
                                .javaType(Integer.class)
                                .length(3)
                                .nullable(true)
                                .defaultValue(null)
                                .build(),
                        ColumnMeta.builder()
                                .name("email")
                                .javaType(String.class)
                                .length(50)
                                .nullable(true)
                                .defaultValue(null)
                                .build()
                ))
                .indexes(List.of(
                        IndexMeta.builder()
                                .name("primary_key")
                                .type("PRIMARY")
                                .columnNames(List.of("id"))
                                .unique(true)
                                .build()
                ))
                .build();

        // 生成修改表的 DDL SQL
        String updateDdlSql = genUpdateDdlSql(newTableMeta, tableMeta);

        // 打印生成的 DDL SQL
        System.out.println(updateDdlSql);
    }

}

