package org.athena.framework.mybatis.autoGen.pgsql;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.athena.framework.mybatis.annotation.FieldComment;
import org.athena.framework.mybatis.autoGen.BaseSqlTableGenService;
import org.athena.framework.mybatis.autoGen.DatabaseTypeMapConfig;
import org.athena.framework.mybatis.autoGen.FieldInfo;
import org.athena.framework.mybatis.entity.BaseEntity;
import org.athena.framework.mybatis.properties.DefaultMapperProperties;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhouzhitong
 * @since 2024-03-24
 **/
@Slf4j
public class PgSqlTableGenService extends BaseSqlTableGenService {

    public PgSqlTableGenService(DefaultMapperProperties mapperProperties, DatabaseTypeMapConfig config,
                                DataSource dataSource) {
        super(mapperProperties, config, dataSource);
    }

    @Override
    protected boolean isExistTable(String tableName, Statement statement) {
        String sql = "SELECT * FROM information_schema.tables WHERE table_name = '" + tableName + "';";
        try {
            ResultSet resultSet = statement.executeQuery(sql);
            return resultSet.next();
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

    private String updateDdlSql(Class<? extends BaseEntity> target, Statement statement) throws Exception {
        String tableName = getTableName(target);
        StringBuilder sql = new StringBuilder();
        StringBuilder sqlComment = new StringBuilder();

        List<Field> allFields = getAllFieldsAndBase(target);
        List<FieldInfo> fieldInfos = getFieldInfo(allFields);

        String showTableDdl = "SELECT column_name, data_type, column_default, is_nullable FROM information_schema.columns WHERE table_name = '" + tableName + "';";
        ResultSet resultSet = statement.executeQuery(showTableDdl);
        Map<String, FieldInfo> dbFieldMap = new HashMap<>();
        while (resultSet.next()) {
            String columnName = resultSet.getString("column_name");
            String dataType = resultSet.getString("data_type");
            String columnDefault = resultSet.getString("column_default");
            String isNullable = resultSet.getString("is_nullable");
            FieldInfo fieldInfo = new FieldInfo();
            fieldInfo.setName(columnName);
            fieldInfo.setType(dataType);
            fieldInfo.setDefaultValue(columnDefault);
            fieldInfo.setNullable("YES".equals(isNullable));
            dbFieldMap.put(columnName, fieldInfo);
        }

        // 遍历所有字段, 处理新增字段
        for (FieldInfo fieldInfo : fieldInfos) {
            String fieldName = fieldInfo.getName();
            FieldInfo dbField = dbFieldMap.get(fieldName);
            if (dbField != null) {
                continue;
            }
            sql.append("ALTER TABLE ").append(tableName).append(" ADD COLUMN ")
                    .append(fieldName).append(" ").append(fieldInfo.getType());
            if (!fieldInfo.isNullable()) {
                sql.append(" not null");
            }
            if (StringUtils.isNotBlank(fieldInfo.getDefaultValue())) {
                sql.append(" default ").append(fieldInfo.getDefaultValue());
            }
            sql.append(";").append("\n");
            if (fieldInfo.getComment() != null) {
                sqlComment.append("comment on column ").append(tableName).append(".").append(fieldName)
                        .append(" is '").append(fieldInfo.getComment()).append("';").append("\n");
            }
        }
        // 遍历所有字段, 处理删除字段
        for (String fieldName : dbFieldMap.keySet()) {
            boolean exist = false;
            for (FieldInfo info : fieldInfos) {
                if (info.getName().equals(fieldName)) {
                    exist = true;
                    break;
                }
            }
            if (exist) {
                continue;
            }
            sql.append("ALTER TABLE ").append(tableName).append(" DROP COLUMN ")
                    .append(fieldName).append(";").append("\n");
        }

        return sql.toString();
    }

    private String createDdlSql(Class<? extends BaseEntity> target) {
        String tableName = getTableName(target);

        StringBuilder sql = new StringBuilder();
        StringBuilder sqlComment = new StringBuilder();

        sql.append("CREATE TABLE IF NOT EXISTS ").append(tableName).append(" (").append("\n");
        sql.append(genBaseDdlSql());

        sqlComment.append(genBaseDdlSqlComment(tableName));

        List<Field> allFields = getAllFields(target);
        int lastIndex = allFields.size() - 1;

        List<FieldInfo> fieldInfos = getFieldInfo(allFields);
        for (FieldInfo fieldInfo : fieldInfos) {
            sql.append("\t").append(fieldInfo.getName()).append(" ").append(fieldInfo.getType());
            if (!fieldInfo.isNullable()) {
                sql.append(" not null");
            }
            if (StringUtils.isNotBlank(fieldInfo.getDefaultValue())) {
                sql.append(" default ").append(fieldInfo.getDefaultValue());
            }
            if (lastIndex-- > 0) {
                sql.append(",");
            }
            sql.append("\n");
            if (fieldInfo.getComment() != null) {
                sqlComment.append("comment on column ").append(tableName).append(".").append(fieldInfo.getName())
                        .append(" is '").append(fieldInfo.getComment()).append("';").append("\n");
            }

        }

        // 添加结束符
        sql.append(");").append("\n");
        // 开始添加注释
        sql.append(sqlComment);

        return sql.toString();
    }

    protected List<FieldInfo> getFieldInfo(List<Field> fields) {
        List<FieldInfo> fieldInfos = new ArrayList<>();
        for (Field field : fields) {
            FieldInfo fieldInfo = new FieldInfo();
            fieldInfo.setName(getUnderlineName(field.getName()));
            fieldInfo.setType(getFieldType(field));

            FieldComment annotation = field.getAnnotation(FieldComment.class);
            if (annotation != null) {
                fieldInfo.setComment(annotation.comment());
                fieldInfo.setDefaultValue(annotation.defaultValue());
                fieldInfo.setNullable(annotation.isAllowNull());

            } else {
                fieldInfo.setDefaultValue(null);
                fieldInfo.setNullable(true);
            }
            fieldInfos.add(fieldInfo);
        }
        return fieldInfos;
    }

    private String genBaseDdlSql() {
        return """
                    id                  bigserial
                        primary key,
                    create_time         timestamp(3)    default now()       not null,
                    update_time         timestamp(3)    default now()       not null ,
                    created_by          varchar(64)     default 'system'    not null,
                    last_modified_by    varchar(64)     default 'system'    not null,
                    deleted             boolean         default false       not null,
                """;
    }

    private String genBaseDdlSqlComment(String tableName) {
        return "comment on column " + tableName + ".id is '主键';" + "\n" +
                "comment on column " + tableName + ".create_time is '创建时间';" + "\n" +
                "comment on column " + tableName + ".update_time is '更新时间';" + "\n" +
                "comment on column " + tableName + ".created_by is '创建人';" + "\n" +
                "comment on column " + tableName + ".last_modified_by is '最后修改人';" + "\n" +
                "comment on column " + tableName + ".deleted is '逻辑删除标识';" + "\n";
    }

    @Override
    protected String getTableName(Class<?> c) {
        String res = super.getTableName(c);
        // 去掉反引号
        if (res.startsWith("`")) {
            res = res.substring(1);
        }
        if (res.endsWith("`")) {
            res = res.substring(0, res.length() - 1);
        }
        return res;
    }

}
