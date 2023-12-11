package com.zhouzhitong.lib.mapper.autoGen;

import com.zhouzhitong.lib.mapper.autoGen.config.GenTypeConfig;
import com.zhouzhitong.lib.mapper.autoGen.config.MySqlGenDdlConfig;
import com.zhouzhitong.lib.mapper.autoGen.config.FieldTypeMap;
import com.zhouzhitong.lib.mapper.entity.BaseEntity;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.List;

/**
 * @author zhouzhitong
 * @since 2023-11-12
 **/
//@Service
@Slf4j
public class MySqlDdlSqlCreateService extends BaseDdlSqlCreateService {
    private final GenTypeConfig config = new MySqlGenDdlConfig();


    @Override
    protected String createDdlSql(Class<? extends BaseEntity> c) {
        StringBuilder sql = new StringBuilder();

        String tableName = getTableName(c);
        sql.append("CREATE TABLE ").append(tableName).append(" (").append("\n");
        sql.append(genBaseDdlSql());

        List<Field> allFields = getAllFields(c);
        int lastIndex = allFields.size() - 1;
        for (Field field : allFields) {
            Class<?> type = field.getType();
            FieldTypeMap fieldTypeMap = config.get(type);
            if (fieldTypeMap == null) {
                throw new RuntimeException("未知的字段类型: " + type);
            }
            Class<?> subType = getGenericTypeName(field);

            String typeStr = fieldTypeMap.getType(subType);
            if (typeStr == null) {
                throw new RuntimeException("未知的子字段类型: " + type + " subType: " + subType);
            }
            String fieldName = getUnderlineName(field.getName());
            sql.append("\t").append(fieldName).append(" ").append(typeStr).append("");
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

        // TODO 字段注释, 从字段的注释中获取
//        for (Field allField : allFields) {
//            String fieldName = getUnderlineName(allField.getName());
//            String comment = allField.getAnnotation(TableField.class).value();
//            if (!comment.isBlank()) {
//                sql.append("comment on column ").append(tableName).append(".").append(fieldName).append(" is '").append(comment).append("';").append("\n");
//            }
//        }

        return sql.toString();
    }

    private String genBaseDdlSql() {
        return """
                    id  bigint auto_increment comment '自增主键' 
                        primary key,
                    create_time      datetime    default CURRENT_TIMESTAMP not null comment '创建时间',
                    created_by       varchar(64) default 'system'          null comment '创建人',
                    deleted          tinyint(1)  default 0                 not null comment '是否被删除（0、否  1、是）',
                    last_modified_by varchar(64) default 'system'          null comment '最后修改人',
                    update_time      datetime    default CURRENT_TIMESTAMP not null comment '最后修改时间',
                """;
    }

}
