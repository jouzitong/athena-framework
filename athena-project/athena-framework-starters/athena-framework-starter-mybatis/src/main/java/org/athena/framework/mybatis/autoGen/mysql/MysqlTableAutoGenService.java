package org.athena.framework.mybatis.autoGen.mysql;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.extern.slf4j.Slf4j;
import org.arthena.framework.common.utils.FileUtils;
import org.arthena.framework.common.utils.PackageUtil;
import org.arthena.framework.common.utils.SystemUtils;
import org.athena.framework.mybatis.annotation.FieldComment;
import org.athena.framework.mybatis.autoGen.DatabaseTypeMapConfig;
import org.athena.framework.mybatis.entity.BaseEntity;
import org.athena.framework.mybatis.properties.DefaultMapperProperties;
import org.athena.framework.mybatis.properties.FieldTypeMap;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhouzhitong
 * @since 2024-01-26
 **/
@Component
@Slf4j
public class MysqlTableAutoGenService extends BaseMysqlGenService implements CommandLineRunner {

    public MysqlTableAutoGenService(DefaultMapperProperties mapperProperties, DatabaseTypeMapConfig config,
                                    DataSource dataSource) {
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
            if (subClass == BaseEntity.class) {
                continue;
            }
            // 获取表名
            String tableName = getTableName(subClass);
            // 查看表是否存在, 如果存在则生成更新表结构的sql
            String sql = "SHOW FULL COLUMNS FROM " + tableName + " ;";
            boolean isExist = statement.executeQuery(sql).next();
            if (isExist) {
                String ddlSql = updateDdlSql((Class<? extends BaseEntity>) subClass, tableName, statement);
                updateBw.write("-- " + subClass.getName() + "\n");
                updateBw.write(ddlSql);
                updateBw.write("\n\n");
            }

            String ddlSql = createDdlSql((Class<? extends BaseEntity>) subClass);
            createBw.write("-- " + subClass.getName() + "\n");
            createBw.write(ddlSql);
            createBw.write("\n\n");


        }
        try {
            createBw.flush();
            createBw.close();
            updateBw.flush();
            updateBw.close();
            LOGGER.info("生成表结构文件成功, 文件路径: {}, 文件名: {}", filePath, mapperProperties.getTableDDLPathFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String updateDdlSql(Class<? extends BaseEntity> c, String tableName, Statement statement) throws Exception {
        // 获取所有字段
        List<Field> allFields = getAllFieldsAndBase(c);

        StringBuilder sb = new StringBuilder();

        // 查看表结构
        String sql = "SHOW FULL COLUMNS FROM " + tableName + ";";
        ResultSet resultSet = statement.executeQuery(sql);
        // 遍历结果
        Map<String, String> dbFieldMap = new HashMap<>();
        while (resultSet.next()) {
            String fieldName = resultSet.getString("Field");
            String fieldType = resultSet.getString("Type");
            dbFieldMap.put(fieldName, fieldType);
        }

        // 遍历所有字段
        for (Field field : allFields) {
            String fieldName = field.getName();
            fieldName = getUnderlineName(fieldName);
            String fieldType = dbFieldMap.get(fieldName);
            if (fieldType != null) {
                continue;
            }
            sb.append("ALTER TABLE ").append(tableName)
                    .append(" ADD COLUMN ").append(fieldName).append(" ")
                    .append(getFieldType(field)).append(";\n");
        }

        return sb.toString();
    }

    private String createDdlSql(Class<? extends BaseEntity> c) {
        StringBuilder sql = new StringBuilder();

        String tableName = getTableName(c);
        sql.append("CREATE TABLE IF NOT EXISTS ").append(tableName).append(" (").append("\n");
        sql.append(genBaseDdlSql());

        List<Field> allFields = getAllFields(c);
        int lastIndex = allFields.size() - 1;
        for (Field field : allFields) {
            String fieldName = getUnderlineName(field.getName());

            FieldComment fieldComment = field.getAnnotation(FieldComment.class);
            if (fieldComment.value() != null) {
                sql.append("\t`").append(fieldName).append("`\t").append(fieldComment.value());
                if (lastIndex-- > 0) {
                    sql.append(",");
                }
                sql.append("\n");
                continue;
            }

            Class<?> type = field.getType();
            FieldTypeMap fieldTypeMap = config.get(type);
            if (fieldTypeMap == null) {
                TableField tableField = field.getAnnotation(TableField.class);
                if (tableField != null && tableField.typeHandler() != null) {

                } else {
                    throw new RuntimeException("未知的字段类型: " + type);
                }
            }
            Class<?> subType = getGenericTypeName(field);

            String typeStr = fieldTypeMap.getType(subType);
            if (typeStr == null) {
                throw new RuntimeException("未知的子字段类型: " + type + " subType: " + subType);
            }
            sql.append("\t").append(fieldName).append(" ").append(typeStr);
            if (lastIndex-- > 0) {
                sql.append(",");
            }
            sql.append("\n");
        }
        sql.append(");").append("\n");

        // 表名注释, 从类的注释中获取
        String tableComment = getTableComment(c);
        if (!tableComment.isBlank()) {
            sql.append("comment on table ").append(tableName).append(" is '").append(tableComment).append("';").append("\n");
        }

        return sql.toString();
    }

    private String genBaseDdlSql() {
        return """
                    `id`  bigint auto_increment comment '自增主键' primary key,
                    `create_time`      datetime    default CURRENT_TIMESTAMP not null comment '创建时间',
                    `created_by`       varchar(64) default 'system'          null comment '创建人',
                    `deleted`          tinyint(1)  default 0                 not null comment '是否被删除（0、否  1、是）',
                    `last_modified_by` varchar(64) default 'system'          null comment '最后修改人',
                    `update_time`      datetime    default CURRENT_TIMESTAMP not null comment '最后修改时间',
                """;
    }

    private String getTableComment(Class<? extends BaseEntity> c) {
        FieldComment annotation = c.getAnnotation(FieldComment.class);
        if (annotation == null) {
            return "";
        }
        return annotation.value();
    }

}
