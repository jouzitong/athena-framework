package org.athena.framework.mybatis.autoGen.mysql;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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

import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * @author zhouzhitong
 * @since 2024-01-26
 **/
@Component
@AllArgsConstructor
@Slf4j
public class MysqlTableAutoGenService implements CommandLineRunner {

    private final DatabaseTypeMapConfig config;

    private final DefaultMapperProperties mapperProperties;

    @Override
    public void run(String... args) throws Exception {
        if (!mapperProperties.isAutoUpdateTable()) {
            LOGGER.info("未开启自动更新表. 如果需要开启, 请在配置文件中设置 lib.mapper.auto-gen.auto-update-table=true");
            return;
        }
        // 获取启动目录
        String userDir = SystemUtils.getDir();
        // 如果不是以 / 开头, 则添加 /
        if (!mapperProperties.getTableDDLPathFile().startsWith("/")) {
            userDir += "/";
        }
        String filePath = userDir + mapperProperties.getTableDDLPathFile();
        BufferedWriter bw = FileUtils.getFileOutputStream(filePath);
        List<Class<?>> subClasses = PackageUtil.getSubClasses(BaseEntity.class, mapperProperties.getBaseEntityPackages());

        for (Class<?> subClass : subClasses) {
            if (subClass == BaseEntity.class) {
                continue;
            }
            String ddlSql = createDdlSql((Class<? extends BaseEntity>) subClass);
            try {
                bw.write("-- " + subClass.getName() + "\n");
                bw.write(ddlSql);
                bw.write("\n\n");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            bw.flush();
            bw.close();
            LOGGER.info("生成表结构文件成功, 文件路径: {}, 文件名: {}", filePath, mapperProperties.getTableDDLPathFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String createDdlSql(Class<? extends BaseEntity> c) {
        StringBuilder sql = new StringBuilder();

        String tableName = getTableName(c);
        sql.append("CREATE TABLE ").append(tableName).append(" (").append("\n");
        sql.append(genBaseDdlSql());

        List<Field> allFields = getAllFields(c);
        int lastIndex = allFields.size() - 1;
        for (Field field : allFields) {
            String fieldName = getUnderlineName(field.getName());

            FieldComment fieldComment = field.getAnnotation(FieldComment.class);
            if (fieldComment.value() != null) {
                sql.append("\t").append(fieldName).append("\t").append(fieldComment.value());
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
                    id  bigint auto_increment comment '自增主键' primary key,
                    create_time      datetime    default CURRENT_TIMESTAMP not null comment '创建时间',
                    created_by       varchar(64) default 'system'          null comment '创建人',
                    deleted          tinyint(1)  default 0                 not null comment '是否被删除（0、否  1、是）',
                    last_modified_by varchar(64) default 'system'          null comment '最后修改人',
                    update_time      datetime    default CURRENT_TIMESTAMP not null comment '最后修改时间',
                """;
    }

    private String getTableComment(Class<? extends BaseEntity> c) {
        FieldComment annotation = c.getAnnotation(FieldComment.class);
        if (annotation == null) {
            return "";
        }
        return annotation.value();
    }

    private String getTableName(Class<? extends BaseEntity> c) {
        TableName annotation = c.getAnnotation(TableName.class);
        if (annotation == null || StringUtils.isBlank(annotation.value())) {
            // 类名的驼峰转下划线
            String name = c.getSimpleName();
            return getUnderlineName(name);
        }

        return annotation.value();
    }

    private List<Field> getAllFields(Class<?> c) {
        List<Field> allFields = new LinkedList<>();
        while (c != BaseEntity.class) {
            Field[] fields = c.getDeclaredFields();
            allFields.addAll(Arrays.asList(fields));
            c = c.getSuperclass();
        }
        return allFields;
    }

    // Ljava/util/List<Ljava/lang/Long;>;
    private Class<?> getGenericTypeName(Field field) {
        Type genericType = field.getGenericType();
        String signature = genericType.getTypeName();
        // 如果没有 < 和 > 返回空
        if (!signature.contains("<") && !signature.contains(">")) {
            return null;
        }
        int begin = signature.indexOf("<");
        int end = signature.indexOf(">");
        String substring = signature.substring(begin + 1, end);
        try {
            return Class.forName(substring);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将字符转换成驼峰命名(小写+下划线)
     */
    private String getUnderlineName(String name) {
        StringBuilder result = new StringBuilder();
        if (name != null && name.length() > 0) {
            // 将第一个字符处理成大写
            result.append(name.substring(0, 1).toLowerCase());
            // 循环处理其余字符
            for (int i = 1; i < name.length(); i++) {
                String s = name.substring(i, i + 1);
                // 在大写字母前添加下划线
                if (s.equals(s.toUpperCase()) && !Character.isDigit(s.charAt(0))) {
                    result.append("_");
                }
                // 其他字符直接转成小写
                result.append(s.toLowerCase());
            }
        }
        return result.toString();
    }

}
