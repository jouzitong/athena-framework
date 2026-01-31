package org.athena.framework.data.mybatis.create;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.arthena.framework.common.properties.CommonProperties;
import org.arthena.framework.common.utils.FileUtils;
import org.arthena.framework.common.utils.PackageUtil;
import org.athena.framework.data.jdbc.entity.IEntity;
import org.athena.framework.data.jdbc.properties.DefaultJdbcProperties;
import org.arthena.framework.common.utils.CamelCaseUtils;
import org.athena.framework.data.mybatis.utils.JdbcUtils;
import org.athena.framework.data.mybatis.create.bean.ClassTableInfo;
import org.athena.framework.data.mybatis.create.bean.DbTableColumn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

import javax.sql.DataSource;
import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

/**
 * @author zhouzhitong
 * @since 2025/7/13
 **/
@Slf4j
@Deprecated
public abstract class BaseDdlCreateService implements IDdlCreateService, CommandLineRunner {

    @Resource
    protected DefaultJdbcProperties jdbcProperties;

    @Autowired
    protected CommonProperties commonProperties;

    @Autowired
    protected DataSource dataSource;

    /**
     * 数据库注释符号
     */
    protected static final String COMMENT_SYMBOL = "-- ";

    @Override
    public void run(String... args) throws Exception {
        Statement statement = null;
        try {
            statement = dataSource.getConnection().createStatement();
            generateTableDdl(statement);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
    }

    protected final void generateTableDdl(Statement statement) {
        if (!jdbcProperties.isEnableCreateTableDdl()) {
            LOGGER.info("未开启自动生成表结构. 如果需要开启, 请在配置文件中设置 lib.jdbc.enableCreateTableDdl=true");
            return;
        }

        // 创建表结构
        StringBuilder createDdlSql = new StringBuilder();

        // 更新表结构
        StringBuilder updateDdlSql = new StringBuilder();


        List<Class<IEntity>> subClasses = getSubClasses(IEntity.class);
        for (Class<?> clazz : subClasses) {
            ClassTableInfo classTableInfo = JdbcUtils.buildClassTableInfo(clazz);
            String tableName = JdbcUtils.getTableName(classTableInfo, jdbcProperties.getIgnorePrefix(), jdbcProperties.getIgnoreSubfix());

            // 生成建表DDL
            String createTableDdl = getCreateTableDdl(classTableInfo);
            // 生成注释
            createDdlSql.append(COMMENT_SYMBOL).append(clazz.getName()).append("\n");
            // 生成建表语句
            createDdlSql.append(createTableDdl).append("\n");

            // 获取数据库中的表字段信息
            Map<String, DbTableColumn> dbTableColumns = getDbTableColumns(tableName, statement);
            String updateTableDdl = null;
            if (MapUtils.isNotEmpty(dbTableColumns)) {
                updateTableDdl = getUpdateTableDdl(classTableInfo, dbTableColumns);
                updateDdlSql.append(COMMENT_SYMBOL).append(clazz.getName()).append("\n");
                updateDdlSql.append(updateTableDdl).append("\n");
            }
            if (jdbcProperties.isAutoUpdateTable()) {
                try {
                    if (!isExistTable(tableName, statement)) {
                        statement.execute(createTableDdl);
                    }
                    if (updateTableDdl != null) {
                        statement.execute(updateTableDdl);
                    }

                } catch (Exception e) {
                    LOGGER.error("更新表结构失败: {}", e.getMessage());
                    throw new RuntimeException(e);
                }
            }

        }
        try {
            doWriteDdlSql(createDdlSql, updateDdlSql);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (jdbcProperties.isAutoUpdateTable()) {
            LOGGER.info("开始更新表结构");
            try {
                statement.execute(createDdlSql.toString());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            LOGGER.info("更新表结构完成");
        }


    }


    private void doWriteDdlSql(StringBuilder createDdlSql, StringBuilder updateDdlSql) throws IOException {
        String filePath = jdbcProperties.getTableDDLPathFile();
        String version = commonProperties.getVersion();
        String createTableDdlFileStr = FileUtils.getPathSplicing(filePath, version, "create_table_ddl.sql");
        String updateTableDdlStr = FileUtils.getPathSplicing(filePath, version, "update_table_ddl.sql");
        LOGGER.info("生成表结构目录: {}", FileUtils.getPathSplicing(filePath, version));


        try (BufferedWriter createBw = FileUtils.getFileOutputStream(createTableDdlFileStr);
             BufferedWriter updateBw = FileUtils.getFileOutputStream(updateTableDdlStr);) {
            createBw.write(createDdlSql.toString());
            createBw.newLine();
            updateBw.write(updateDdlSql.toString());
            updateBw.newLine();
        }

    }

    protected String getUpdateTableDdl(ClassTableInfo classTableInfo, Map<String, DbTableColumn> dbTableColumns) {
        StringBuilder sb = new StringBuilder();
        sb.append(COMMENT_SYMBOL).append("新增字段");
        // 遍历数据库字段, 获取新增字段
        for (Field field : classTableInfo.getColumns()) {
            String columnNameCamelCase = CamelCaseUtils.toCamelCase(field.getName());
            if (dbTableColumns.containsKey(columnNameCamelCase)) {
                continue;
            }
            sb.append(getAddColumnDdl(field)).append(";\n");
        }
        List<Field> columns = classTableInfo.getColumns();

        sb.append(COMMENT_SYMBOL).append("修改字段");
        // 遍历数据库字段, 获取删除字段
        for (String dbColumn : dbTableColumns.keySet()) {
            String camelCaseName = CamelCaseUtils.toCamelCase(dbColumn);
            boolean exist = false;
            for (Field fieldColumn : columns) {
                if (fieldColumn.getName().equals(camelCaseName)) {
                    exist = true;
                    break;
                }
            }
            if (!exist) {
                sb.append(getDropColumnDdl(dbColumn)).append(";\n");
            }
        }

        sb.append(COMMENT_SYMBOL).append("  表结构字段更新");
        // 遍历数据库字段, 获取修改字段
        for (Field fieldColumn : classTableInfo.getColumns()) {
            String columnNameSnakeCase = CamelCaseUtils.toSnakeCase(fieldColumn.getName());
            DbTableColumn dbTableColumn = dbTableColumns.get(columnNameSnakeCase);
            if (dbTableColumn == null) {
                continue;
            }
            // TODO
        }

        return sb.toString();
    }

    protected String getCreateTableDdl(ClassTableInfo classTableInfo) {
        StringBuilder sb = new StringBuilder();
        String headerTable = getHeaderTable(classTableInfo);
        sb.append(headerTable).append(" (\n");
        sb.append(getAllColumnDdl(classTableInfo)).append("\n");
        sb.append(");").append("\n");

        sb.append(getAllIndexDdl(classTableInfo));

        return sb.toString();
    }


    /**
     * 获取表头 DDL
     *
     * @param tableInfo 表 Class 信息
     * @return 表头 DDL
     */
    protected String getHeaderTable(ClassTableInfo tableInfo) {
        String tableName = JdbcUtils.getTableName(tableInfo, jdbcProperties.getIgnorePrefix(), jdbcProperties.getIgnoreSubfix());
        return "create table if not exists " + tableName;
    }

    /**
     * 获取所有的字段 DDL
     *
     * @param tableInfo 表 Class 信息
     * @return 字段 DDL
     */
    protected abstract String getAllColumnDdl(ClassTableInfo tableInfo);

    /**
     * 获取新增字段 DDL
     *
     * @param field 字段
     * @return 字段 DDL
     */
    protected abstract String getAddColumnDdl(Field field);

    /**
     * 获取更新字段 DDL
     *
     * @param field 字段
     * @return 字段 DDL
     */
    protected abstract String getUpdateColumnDdl(Field field);

    /**
     * 获取删除字段 DDL
     *
     * @param columnName 字段
     * @return 字段 DDL
     */
    protected abstract String getDropColumnDdl(String columnName);

    /**
     * 获取索引 DDL
     *
     * @param tableInfo 表 Class 信息
     * @return 索引 DDL
     */
    protected abstract String getAllIndexDdl(ClassTableInfo tableInfo);

    /**
     * 判断表是否存在
     *
     * @param tableName 表名
     * @param statement 语句
     * @return 是否存在
     */
    protected abstract boolean isExistTable(String tableName, Statement statement);

    /**
     * 获取数据库中的表字段信息
     *
     * @param tableName 表名
     * @param statement Statement
     * @return 表字段信息. 如果表不存在, 则返回 Map.empty()
     * key: 数据库列名
     * value: DbTableColumn
     */
    protected abstract Map<String, DbTableColumn> getDbTableColumns(String tableName, Statement statement);

    protected <T> List<Class<T>> getSubClasses(Class<T> clazz) {
        return PackageUtil.getSubClasses(clazz, jdbcProperties.getBaseEntityPackages());
    }

}
