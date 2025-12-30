package org.athena.framework.data.mybatis.utils;

import org.athena.framework.data.jdbc.type.DbType;
import org.athena.framework.data.mybatis.bean.TableMeta;
import org.athena.framework.data.mybatis.bean.meta.ColumnMeta;
import org.athena.framework.data.mybatis.bean.meta.IndexMeta;

import java.util.Arrays;
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

        // 添加新列
        addNewColumns(sb, newTableMeta, oldTableMeta);

        // 修改现有列
        modifyExistingColumns(sb, newTableMeta, oldTableMeta);

        // 删除移除的列
        dropRemovedColumns(sb, newTableMeta, oldTableMeta);

        // 添加新索引
        addNewIndexes(sb, newTableMeta, oldTableMeta);

        // 修改现有索引
        modifyExistingIndexes(sb, newTableMeta, oldTableMeta);

        // 删除移除的索引
        dropRemovedIndexes(sb, newTableMeta, oldTableMeta);

        // 移除最后一个逗号和换行
        if (sb.charAt(sb.length() - 1) == ',') {
            sb.setLength(sb.length() - 2);
        }

        sb.append(";");

        return sb.toString();
    }

    private static void addNewColumns(StringBuilder sb, TableMeta newTableMeta, TableMeta oldTableMeta) {
        List<ColumnMeta> newColumns = newTableMeta.getColumns().stream()
                .filter(column -> !oldTableMeta.getColumns().contains(column))
                .collect(Collectors.toList());
        if (!newColumns.isEmpty()) {
            sb.append(COMMENT_SYMBOL).append(" 新增字段\n");
            sb.append("ADD (").append(String.join(",\n", newColumns.stream()
                    .map(column -> buildColumnDefinition(column, DbType.MYSQL))
                    .collect(Collectors.toList()))).append("),\n");
        }
    }

    private static void modifyExistingColumns(StringBuilder sb, TableMeta newTableMeta, TableMeta oldTableMeta) {
        List<ColumnMeta> modifiedColumns = newTableMeta.getColumns().stream()
                .filter(newColumn -> oldTableMeta.getColumns().stream()
                        .anyMatch(oldColumn -> !newColumn.equals(oldColumn) && newColumn.getName().equals(oldColumn.getName())))
                .collect(Collectors.toList());
        if (!modifiedColumns.isEmpty()) {
            sb.append(COMMENT_SYMBOL).append(" 修改字段\n");
            for (ColumnMeta column : modifiedColumns) {
                sb.append(buildColumnAlterDefinition(column, DbType.MYSQL)).append(",\n");
            }
        }
    }

    private static void dropRemovedColumns(StringBuilder sb, TableMeta newTableMeta, TableMeta oldTableMeta) {
        List<ColumnMeta> removedColumns = oldTableMeta.getColumns().stream()
                .filter(column -> !newTableMeta.getColumns().contains(column))
                .collect(Collectors.toList());
        if (!removedColumns.isEmpty()) {
            sb.append(COMMENT_SYMBOL).append(" 删除字段\n");
            for (ColumnMeta column : removedColumns) {
                sb.append("DROP COLUMN `").append(column.getName()).append("`,\n");
            }
        }
    }

    private static void addNewIndexes(StringBuilder sb, TableMeta newTableMeta, TableMeta oldTableMeta) {
        List<IndexMeta> newIndexes = newTableMeta.getIndexes().stream()
                .filter(index -> !oldTableMeta.getIndexes().contains(index))
                .collect(Collectors.toList());
        if (!newIndexes.isEmpty()) {
            sb.append(COMMENT_SYMBOL).append(" 新增索引\n");
            for (IndexMeta index : newIndexes) {
                sb.append(buildIndexAlterDefinition(index, "ADD")).append(",\n");
            }
        }
    }

    private static void modifyExistingIndexes(StringBuilder sb, TableMeta newTableMeta, TableMeta oldTableMeta) {
        List<IndexMeta> modifiedIndexes = newTableMeta.getIndexes().stream()
                .filter(newIndex -> oldTableMeta.getIndexes().stream()
                        .anyMatch(oldIndex -> !newIndex.equals(oldIndex) && newIndex.getName().equals(oldIndex.getName())))
                .collect(Collectors.toList());
        if (!modifiedIndexes.isEmpty()) {
            sb.append(COMMENT_SYMBOL).append(" 修改索引\n");
            for (IndexMeta index : modifiedIndexes) {
                sb.append(buildIndexAlterDefinition(index, "ALTER")).append(",\n");
            }
        }
    }

    private static void dropRemovedIndexes(StringBuilder sb, TableMeta newTableMeta, TableMeta oldTableMeta) {
        List<IndexMeta> removedIndexes = oldTableMeta.getIndexes().stream()
                .filter(index -> !newTableMeta.getIndexes().contains(index))
                .collect(Collectors.toList());
        if (!removedIndexes.isEmpty()) {
            sb.append(COMMENT_SYMBOL).append(" 删除索引\n");
            for (IndexMeta index : removedIndexes) {
                sb.append("DROP INDEX ").append(index.getName()).append(",\n");
            }
        }
    }

    private static String buildColumnDefinition(ColumnMeta column, DbType dbType) {
        StringBuilder columnDef = new StringBuilder("  ");
        columnDef
                .append("`").append(column.getName()).append("` ")
                .append(dbType.getType(column.getJavaType(), column.getLength()));

        if (column.isNullable()) {
            columnDef.append(" NULL");
        } else {
            columnDef.append(" NOT NULL");
        }

        if (column.getDefaultValue() != null) {
            columnDef.append(" DEFAULT ").append(column.getDefaultValue());
        }
        // comment
        if (column.getComment() != null) {
            columnDef.append(" COMMENT ").append(column.getComment());
        }

        return columnDef.toString();
    }

    private static String buildColumnAlterDefinition(ColumnMeta column, DbType dbType) {
        StringBuilder columnDef = new StringBuilder("  MODIFY COLUMN ");
        columnDef.append("`").append(column.getName()).append("` ").append(dbType.getType(column.getJavaType(), column.getLength()));

        if (column.isNullable()) {
            columnDef.append(" NULL");
        } else {
            columnDef.append(" NOT NULL");
        }

        if (column.getDefaultValue() != null) {
            columnDef.append(" DEFAULT ").append(column.getDefaultValue());
        }
        // comment
        if (column.getComment() != null) {
            columnDef.append(" COMMENT '").append(column.getComment()).append("'");
        }

        return columnDef.toString();
    }

    private static String buildIndexAlterDefinition(IndexMeta index, String action) {
        StringBuilder indexDef = new StringBuilder("  ").append(action).append(" INDEX ");
        indexDef.append("`").append(index.getName()).append("` (");
        indexDef.append(String.join(", ", index.getColumnNames()));
        indexDef.append(")");

        return indexDef.toString();
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

