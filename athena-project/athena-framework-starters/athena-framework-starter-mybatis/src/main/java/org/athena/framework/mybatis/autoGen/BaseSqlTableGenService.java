package org.athena.framework.mybatis.autoGen;

import lombok.extern.slf4j.Slf4j;
import org.arthena.framework.common.utils.FileUtils;
import org.arthena.framework.common.utils.PackageUtil;
import org.arthena.framework.common.utils.SystemUtils;
import org.athena.framework.mybatis.entity.BaseEntity;
import org.athena.framework.mybatis.properties.DefaultMapperProperties;
import org.springframework.boot.CommandLineRunner;

import javax.sql.DataSource;
import java.io.BufferedWriter;
import java.sql.Statement;
import java.util.List;

/**
 * @author zhouzhitong
 * @since 2024-03-22
 **/
@Slf4j
public abstract class BaseSqlTableGenService extends BaseGenService implements CommandLineRunner {

    public BaseSqlTableGenService(DefaultMapperProperties mapperProperties,
                                  DatabaseTypeMapConfig config, DataSource dataSource) {
        super(mapperProperties, config, dataSource);
    }

    @Override
    public void run(String... args) throws Exception {
        if (!mapperProperties.isEnableCreateTableDdl()) {
            LOGGER.info("未开启自动更新表. 如果需要开启, 请在配置文件中设置 lib.mapper.auto-gen.enableCreateTableDdl=true");
            return;
        }
        LOGGER.info("开始生成表结构文件...");

        // 获取启动目录
        String userDir = SystemUtils.getDir();
        // 如果不是以 / 开头, 则添加 /
        if (!mapperProperties.getTableDDLPathFile().startsWith("/")) {
            userDir += "/";
        }
        String filePath = userDir + mapperProperties.getTableDDLPathFile();
        String createTableDdlFileStr = filePath + "/create_table_ddl.sql";
        String updateTableDdlStr = filePath + "/update_table_ddl.sql";

        BufferedWriter createBw = FileUtils.getFileOutputStream(createTableDdlFileStr);
        BufferedWriter updateBw = FileUtils.getFileOutputStream(updateTableDdlStr);

        List<Class<?>> subClasses = PackageUtil.getSubClasses(BaseEntity.class, mapperProperties.getBaseEntityPackages());

        Statement statement = dataSource.getConnection().createStatement();

        for (Class<?> subClass : subClasses) {
            Class<? extends BaseEntity> targetClass = (Class<? extends BaseEntity>) subClass;
            if (subClass == BaseEntity.class) {
                continue;
            }
            String tableName = getTableName(targetClass);
            boolean isExist = false;
            if (isExistTable(tableName, statement)) {
                isExist = true;
            }

            String createDdl = getCreateTableDdl(targetClass);
            String updateDdl = null;
            if (isExist) {
                updateDdl = getUpdateTableDdl(targetClass, statement);
            }
            if (createDdl == null) {
                continue;
            }

            if (mapperProperties.isAutoUpdateTable()) {
                // 如果表存在, 则生成更新表结构的sql
                if (isExistTable(tableName, statement)) {
                    try {
                        statement.execute(createDdl);
                        LOGGER.info("创建表: {}", tableName);
                    } catch (Exception e) {
                        LOGGER.error("创建表 {} 失败: {}", tableName, e.getMessage());
                    }
                }
                if (updateDdl != null) {
                    try {
                        statement.execute(updateDdl);
                        LOGGER.info("更新表: {}", tableName);
                    } catch (Exception e) {
                        LOGGER.error("更新表 {} 失败: {}", tableName, e.getMessage());
                    }
                }
            }
            createBw.write(createDdl);
            createBw.newLine();
            if (updateDdl != null) {
                updateBw.write(updateDdl);
                updateBw.newLine();
            }
        }

        try {
            createBw.flush();
            createBw.close();
            updateBw.flush();
            updateBw.close();
        } catch (Exception e) {
            LOGGER.error("关闭流失败: {}", e.getMessage());
        }

    }

    /**
     * 判断表是否存在
     *
     * @param tableName 表名
     * @param statement Statement
     * @return 是否存在
     */
    protected abstract boolean isExistTable(String tableName, Statement statement);

    /**
     * 获取创建表的DDL
     *
     * @param target 目标类
     * @return DDL
     */
    protected abstract String getCreateTableDdl(Class<? extends BaseEntity> target);

    /**
     * 获取更新表的DDL
     *
     * @param target    目标类
     * @param statement Statement
     * @return DDL
     */
    protected abstract String getUpdateTableDdl(Class<? extends BaseEntity> target, Statement statement);


}
