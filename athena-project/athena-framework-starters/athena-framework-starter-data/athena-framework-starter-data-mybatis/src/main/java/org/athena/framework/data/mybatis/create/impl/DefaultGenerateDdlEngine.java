package org.athena.framework.data.mybatis.create.impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.arthena.framework.common.properties.CommonProperties;
import org.arthena.framework.common.utils.FileUtils;
import org.arthena.framework.common.utils.PackageUtil;
import org.athena.framework.data.jdbc.entity.IEntity;
import org.athena.framework.data.jdbc.properties.DefaultJdbcProperties;
import org.athena.framework.data.mybatis.bean.TableMeta;
import org.athena.framework.data.mybatis.create.builder.ITableMetaBuilder;
import org.athena.framework.data.mybatis.create.builder.impl.DefaultTableMetaBuilder;
import org.athena.framework.data.mybatis.create.IGenerateDdlEngine;
import org.athena.framework.data.mybatis.utils.MysqlJdbcDdlUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.BufferedWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

@Service
@Slf4j
public class DefaultGenerateDdlEngine implements IGenerateDdlEngine, CommandLineRunner {

    @Resource
    protected DefaultJdbcProperties jdbcProperties;

    @Autowired
    protected CommonProperties commonProperties;

    @Autowired
    protected DataSource dataSource;

    /**
     * 数据库注释符号
     */
    public static final String COMMENT_SYMBOL = "-- ";

    // ================= 自定义变量 =================
    protected Statement statement;

    protected List<Class<?>> subClasses;

    @Override
    public void run(String... args) throws Exception {
        if (!jdbcProperties.isEnableCreateTableDdl()) {
            LOGGER.info("未开启自动生成表结构. 如果需要开启, 请在配置文件中设置 lib.jdbc.enableCreateTableDdl=true");
            return;
        }
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
        List<Class<?>> subClasses = getSubClasses(IEntity.class);
        ITableMetaBuilder tableBuilder = new DefaultTableMetaBuilder();
        for (Class<?> clazz : subClasses) {
            // TODO 应该获取自定义解析器, 从spring ioc 中获取
//            tableBuilder.addParser(null);
            tableBuilder.clazz(clazz);
            TableMeta tableMeta = tableBuilder.build();
            // FIXME 当前只支持mysql.
            //  待优化: 应该定义一个 ddl sql 生成器, 根据 数据库类型获取不同的生成器, 并生成 SQL

            String createTableSql = MysqlJdbcDdlUtils.genCreateDdlSql(tableMeta);
            createDdlSql.append(COMMENT_SYMBOL).append(clazz.getName()).append("\n")
                    .append(createTableSql).append("\n");

            // TODO 更新的SQL语句待完成
            String updateTableSql = null;

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
            this.statement = dataSource.getConnection().createStatement();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (this.statement != null) {
                this.statement.close();
            }
        }
    }

    protected List<Class<?>> getSubClasses(Class<?> clazz) {
        return PackageUtil.getSubClasses(clazz, jdbcProperties.getBaseEntityPackages());
    }

}
