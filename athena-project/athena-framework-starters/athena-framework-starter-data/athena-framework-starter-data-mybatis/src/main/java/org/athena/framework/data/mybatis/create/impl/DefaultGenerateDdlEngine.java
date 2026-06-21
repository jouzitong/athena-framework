package org.athena.framework.data.mybatis.create.impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.arthena.framework.common.properties.CommonProperties;
import org.arthena.framework.common.utils.FileUtils;
import org.arthena.framework.common.utils.PackageUtil;
import org.athena.framework.data.jdbc.entity.IEntity;
import org.athena.framework.data.jdbc.properties.DefaultJdbcProperties;
import org.athena.framework.data.mybatis.annotations.DdlIgnoreTable;
import org.athena.framework.data.mybatis.bean.TableMeta;
import org.athena.framework.data.mybatis.bean.meta.ColumnMeta;
import org.athena.framework.data.mybatis.create.IGenerateDdlEngine;
import org.athena.framework.data.mybatis.create.builder.ITableMetaBuilder;
import org.athena.framework.data.mybatis.create.builder.impl.DefaultTableMetaBuilder;
import org.athena.framework.data.mybatis.utils.MysqlJdbcDdlUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.BufferedWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class DefaultGenerateDdlEngine implements IGenerateDdlEngine, CommandLineRunner {

    @Resource
    protected DefaultJdbcProperties jdbcProperties;

    @Autowired
    protected CommonProperties commonProperties;

    @Autowired
    protected Environment environment;

    @Autowired
    protected DataSource dataSource;

    /**
     * 数据库注释符号
     */
    public static final String COMMENT_SYMBOL = "-- ";

    // ================= 自定义变量 =================
    protected Connection connection;

    protected Statement statement;

    protected List<Class<IEntity>> subClasses;

    @Override
    public void run(String... args) throws Exception {
        if (!jdbcProperties.isEnableCreateTableDdl()) {
            LOGGER.info("未开启自动生成表结构. 如果需要开启, 请在配置文件中设置 lib.jdbc.enableCreateTableDdl=true");
            return;
        }
        LOGGER.info("DDL start create");
        // 执行准备工作
        prepare();
        try {
            // 开始执行
            startProcess();
        } finally {
            // 最后完成工作
            finish();
        }
    }

    protected void startProcess() throws SQLException, IOException {
        // 创建表结构
        StringBuilder createDdlSql = new StringBuilder();

        // 更新表结构
        StringBuilder updateDdlSql = new StringBuilder();
        List<Class<IEntity>> subClasses = getSubClasses(IEntity.class);
        ITableMetaBuilder tableBuilder = new DefaultTableMetaBuilder();
        for (Class<?> clazz : subClasses) {
            if (isDdlIgnored(clazz)) {
                continue;
            }
            // TODO 应该获取自定义解析器, 从spring ioc 中获取
//            tableBuilder.addParser(null);
            tableBuilder.clazz(clazz);
            TableMeta tableMeta = tableBuilder.build();
            // FIXME 当前只支持mysql.
            //  待优化: 应该定义一个 ddl sql 生成器, 根据 数据库类型获取不同的生成器, 并生成 SQL

            String createTableSql = MysqlJdbcDdlUtils.genCreateDdlSql(tableMeta);
            createDdlSql.append(COMMENT_SYMBOL).append(clazz.getName()).append("\n")
                    .append(createTableSql).append("\n");

            TableMeta oldTableMeta = getCurrentTableMeta(tableMeta.getName());
            String updateTableSql = MysqlJdbcDdlUtils.genUpdateDdlSql(
                    tableMeta,
                    oldTableMeta,
                    jdbcProperties.isAutoUpdateTable() || jdbcProperties.isAutoAddColumn(),
                    jdbcProperties.isAutoUpdateColumn(),
                    jdbcProperties.isAutoDropColumn()
            );
            if (StringUtils.isNotBlank(updateTableSql)) {
                updateDdlSql.append(COMMENT_SYMBOL).append(clazz.getName()).append("\n")
                        .append(updateTableSql).append("\n");
            }

            if (jdbcProperties.isAutoUpdateTable()) {
                if (StringUtils.isNotBlank(createTableSql)) {
                    statement.execute(createTableSql);
                }
                if (StringUtils.isNotBlank(updateTableSql)) {
                    statement.execute(updateTableSql);
                }
            }
        }
        // 创建SQL文件
        doWriteDdlSql(createDdlSql, updateDdlSql);

    }

    private void doWriteDdlSql(StringBuilder createDdlSql, StringBuilder updateDdlSql) throws IOException {
        String filePath = jdbcProperties.getTableDDLPathFile();
        String applicationName = StringUtils.defaultIfBlank(environment.getProperty("spring.application.name"), "application");
        String version = commonProperties.getVersion();
        String createTableDdlFileStr = FileUtils.getPathSplicing(filePath, applicationName, version, "create_table_ddl.sql");
        String updateTableDdlStr = FileUtils.getPathSplicing(filePath, applicationName, version, "update_table_ddl.sql");
        LOGGER.info("生成表结构目录: {}", FileUtils.getPathSplicing(filePath, applicationName, version));

        try (BufferedWriter createBw = FileUtils.getFileOutputStream(createTableDdlFileStr);
             BufferedWriter updateBw = FileUtils.getFileOutputStream(updateTableDdlStr);) {
            createBw.write(createDdlSql.toString());
            createBw.newLine();
            updateBw.write(updateDdlSql.toString());
            updateBw.newLine();
        }

    }

    /**
     * 结束工作
     *
     * @throws Exception Exception
     */
    protected void finish() throws Exception {
        this.subClasses = null;
        try {
            if (this.statement != null) {
                this.statement.close();
            }
            this.statement = null;
            if (this.connection != null) {
                this.connection.close();
            }
            this.connection = null;
        } catch (Exception e) {
            LOGGER.warn("未知异常: ", e);
        }
    }

    /**
     * 准备工作
     *
     * @throws Exception Exception
     */
    protected void prepare() throws Exception {
        this.subClasses = getSubClasses(IEntity.class);
        try {
            this.connection = dataSource.getConnection();
            this.statement = this.connection.createStatement();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    protected <T> List<Class<T>> getSubClasses(Class<T> clazz) {
        return PackageUtil.getSubClasses(clazz, jdbcProperties.getBaseEntityPackages());
    }

    private boolean isDdlIgnored(Class<?> clazz) {
        return clazz.isAnnotationPresent(DdlIgnoreTable.class);
    }

    private TableMeta getCurrentTableMeta(String tableName) throws SQLException {
        if (StringUtils.isBlank(tableName)) {
            return null;
        }
        DatabaseMetaData metaData = connection.getMetaData();
        String catalog = connection.getCatalog();
        String schema = getSchema();
        String actualTableName = findActualTableName(metaData, catalog, schema, tableName);
        if (StringUtils.isBlank(actualTableName)) {
            return null;
        }

        List<ColumnMeta> columns = getColumns(metaData, catalog, schema, actualTableName);
        if (columns.isEmpty() && schema != null) {
            columns = getColumns(metaData, catalog, null, actualTableName);
        }
        return TableMeta.builder()
                .name(actualTableName)
                .columns(columns)
                .build();
    }

    private List<ColumnMeta> getColumns(DatabaseMetaData metaData, String catalog, String schema, String tableName) throws SQLException {
        List<ColumnMeta> columns = new ArrayList<>();
        try (ResultSet resultSet = metaData.getColumns(catalog, schema, tableName, null)) {
            while (resultSet.next()) {
                columns.add(ColumnMeta.builder()
                        .name(resultSet.getString("COLUMN_NAME"))
                        .build());
            }
        }
        return columns;
    }

    private String findActualTableName(DatabaseMetaData metaData, String catalog, String schema, String tableName) throws SQLException {
        String[] candidates = {tableName, tableName.toLowerCase(), tableName.toUpperCase()};
        for (String candidate : candidates) {
            String actualTableName = findActualTableNameBySchema(metaData, catalog, schema, candidate);
            if (StringUtils.isNotBlank(actualTableName)) {
                return actualTableName;
            }
            if (schema != null) {
                actualTableName = findActualTableNameBySchema(metaData, catalog, null, candidate);
            }
            if (StringUtils.isNotBlank(actualTableName)) {
                return actualTableName;
            }
        }
        return null;
    }

    private String findActualTableNameBySchema(DatabaseMetaData metaData, String catalog, String schema, String tableName) throws SQLException {
        try (ResultSet resultSet = metaData.getTables(catalog, schema, tableName, new String[]{"TABLE"})) {
            if (resultSet.next()) {
                return resultSet.getString("TABLE_NAME");
            }
        }
        return null;
    }

    private String getSchema() {
        try {
            return connection.getSchema();
        } catch (Exception e) {
            return null;
        }
    }

}
