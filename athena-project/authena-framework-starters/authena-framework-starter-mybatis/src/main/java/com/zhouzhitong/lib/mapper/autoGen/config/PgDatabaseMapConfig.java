package com.zhouzhitong.lib.mapper.autoGen.config;

import org.arthena.lib.common.base.BaseEnum;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * postgresql 数据库字段类型映射配置
 *
 * @author zhouzhitong
 * @since 2023-11-14
 **/
//@Component
public class PgDatabaseMapConfig implements DatabaseTypeMapConfig {

    /**
     * 字段类型映射
     */
    @Getter
    private final Map<Class<?>, FieldTypeMap> fieldTypeMap;

    @Autowired
    private DefaultMapperProperties mapperProperties;

    public PgDatabaseMapConfig() {
        this.fieldTypeMap = new HashMap<>();
        init();
        List<FieldTypeMap> fieldTypeMaps = mapperProperties.getFieldTypeMaps();
        for (FieldTypeMap typeMap : fieldTypeMaps) {
            fieldTypeMap.put(typeMap.getFieldType(), typeMap);
        }
    }

    public void init() {
        // 初始化 常见的Bean实体字段类型与 postgresql 数据库字段类型的映射
        fieldTypeMap.put(Long.class, new FieldTypeMap(Long.class, "bigint"));
        fieldTypeMap.put(Integer.class, new FieldTypeMap(Integer.class, "integer"));
        fieldTypeMap.put(Short.class, new FieldTypeMap("smallint"));
        fieldTypeMap.put(String.class, new FieldTypeMap(String.class, "varchar(64)"));
        fieldTypeMap.put(LocalDateTime.class, new FieldTypeMap(LocalDateTime.class, "timestamp(3)"));
        fieldTypeMap.put(Boolean.class, new FieldTypeMap(Boolean.class, "boolean"));
        FieldTypeMap list = new FieldTypeMap(List.class, "jsonb");
        list.add(Long.class, "bigint[]");
        list.add(Integer.class, "int[]");
        list.add(String.class, "jsonb");
        fieldTypeMap.put(List.class, list);
        fieldTypeMap.put(BaseEnum.class, new FieldTypeMap(BaseEnum.class, "integer"));
        fieldTypeMap.put(Enum.class, new FieldTypeMap(BaseEnum.class, "integer"));
    }

    @Override
    public FieldTypeMap get(Class<?> c) {
        if (c == null) {
            return fieldTypeMap.get(null);
        }
        if (Object.class == c) {
            return null;
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
