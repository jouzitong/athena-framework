package org.athena.framework.data.mybatis.autoGen.mysql;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.extern.slf4j.Slf4j;
import org.athena.framework.data.jdbc.annotation.FieldComment;
import org.athena.framework.data.mybatis.autoGen.BaseSqlTableGenService;
import org.athena.framework.data.mybatis.autoGen.DatabaseTypeMapConfig;
import org.athena.framework.data.mybatis.entity.BaseEntity;
import org.athena.framework.data.mybatis.properties.DefaultMapperProperties;
import org.athena.framework.data.mybatis.properties.bean.FieldTypeMap;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhouzhitong
 * @since 2024-03-24
 **/
@Slf4j
public class MysqlTableGenService extends BaseSqlTableGenService {

    public MysqlTableGenService(DefaultMapperProperties mapperProperties, DatabaseTypeMapConfig config,
                                DataSource dataSource) {
        super(mapperProperties, config, dataSource);
    }

    @Override
    protected boolean isExistTable(String tableName, Statement statement) {
        String sql = "SHOW FULL COLUMNS FROM " + tableName + " ;";
        try {
            statement.executeQuery(sql);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    protected String getCreateTableDdl(Class<? extends BaseEntity> target) {
        return createDdlSql(target);
    }

    @Override
    protected String getUpdateTableDdl(Class<? extends BaseEntity> target, Statement statement) {
        try {
            return updateDdlSql(target, statement);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String updateDdlSql(Class<? extends BaseEntity> c, Statement statement) throws Exception {
        // 获取所有字段
        List<Field> allFields = getAllFieldsAndBase(c);

        StringBuilder sb = new StringBuilder();

        String tableName = getTableName(c);
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

        // 遍历所有字段, 处理新增字段
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

        // 遍历数据库字段, 处理删除字段
        for (String fieldName : dbFieldMap.keySet()) {
            boolean exist = false;
            for (Field field : allFields) {
                String name = getUnderlineName(field.getName());
                if (name.equals(fieldName)) {
                    exist = true;
                    break;
                }
            }
            if (!exist) {
                sb.append("ALTER TABLE ").append(tableName)
                        .append(" DROP COLUMN ").append(fieldName).append(";\n");
            }
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
                    `version`          int         default 1                 not null comment '版本号',
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
