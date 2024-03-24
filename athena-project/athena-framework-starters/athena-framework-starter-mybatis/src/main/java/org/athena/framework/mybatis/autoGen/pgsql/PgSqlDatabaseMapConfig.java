package org.athena.framework.mybatis.autoGen.pgsql;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.Getter;
import org.arthena.framework.common.base.BaseEnum;
import org.arthena.framework.common.base.DBJson;
import org.athena.framework.mybatis.autoGen.DatabaseTypeMapConfig;
import org.athena.framework.mybatis.properties.DefaultMapperProperties;
import org.athena.framework.mybatis.properties.FieldTypeMap;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * mysql 数据库字段类型映射配置
 *
 * @author zhouzhitong
 * @since 2023-11-14
 **/
@Component
public class PgSqlDatabaseMapConfig implements DatabaseTypeMapConfig {

    /**
     * 字段类型映射
     */
    @Getter
    private final Map<Class<?>, FieldTypeMap> fieldTypeMap;

    @Resource
    private DefaultMapperProperties mapperProperties;

    public PgSqlDatabaseMapConfig() {
        this.fieldTypeMap = new HashMap<>();
        init();
    }

    @PostConstruct
    public void loadTypeMap() {
        List<FieldTypeMap> fieldTypeMaps = mapperProperties.getFieldTypeMaps();
        if (fieldTypeMaps == null) {
            return;
        }
        for (FieldTypeMap typeMap : fieldTypeMaps) {
            fieldTypeMap.put(typeMap.getFieldType(), typeMap);
        }
    }

    private void init() {
        // 初始化 常见的Bean实体字段类型与 pgsql 数据库字段类型的映射
        fieldTypeMap.put(Long.class, new FieldTypeMap(Long.class, "bigint"));
        fieldTypeMap.put(Integer.class, new FieldTypeMap(Integer.class, "int"));
        fieldTypeMap.put(Short.class, new FieldTypeMap("smallint"));
        fieldTypeMap.put(String.class, new FieldTypeMap(String.class, "varchar(64)"));
        fieldTypeMap.put(LocalDateTime.class, new FieldTypeMap(LocalDateTime.class, "datetime"));
        fieldTypeMap.put(Boolean.class, new FieldTypeMap(Boolean.class, "tinyint"));
        FieldTypeMap list = new FieldTypeMap(List.class, "json");
        list.add(Long.class, "bigint[]");
        fieldTypeMap.put(List.class, list);

        fieldTypeMap.put(BaseEnum.class, new FieldTypeMap(BaseEnum.class, "int"));
        fieldTypeMap.put(Object.class, new FieldTypeMap("json"));
        fieldTypeMap.put(DBJson.class, new FieldTypeMap("json"));


    }

    @Override
    public FieldTypeMap get(Class<?> c) {
        if (c == null) {
            return fieldTypeMap.get(null);
        }
        if (Object.class == c) {
            return fieldTypeMap.get(c);
        }
        FieldTypeMap res;
        if ((res = fieldTypeMap.get(c)) != null) {
            return res;
        }
        Class<?>[] interfaces = c.getInterfaces();
        for (Class<?> anInterface : interfaces) {
            res = get(anInterface);
            if (res != null) {
                return res;
            }
        }
        return get(c.getSuperclass());
    }

}
