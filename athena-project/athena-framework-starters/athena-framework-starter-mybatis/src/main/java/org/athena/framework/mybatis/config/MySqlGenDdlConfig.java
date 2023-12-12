package org.athena.framework.mybatis.config;

import lombok.Getter;
import org.arthena.framework.common.base.BaseEnum;
import org.arthena.framework.common.base.DBJson;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhouzhitong
 * @since 2023-11-12
 **/
public class MySqlGenDdlConfig implements GenTypeConfig {

    /**
     * key: 实体字段类型
     * value: 数据库字段类型
     */
    @Getter
    private final Map<Class<?>, FieldTypeMap> fieldTypeMap;

    public MySqlGenDdlConfig() {
        this.fieldTypeMap = new HashMap<>();
        init();
    }

    public void init() {
        // 初始化 常见的Bean实体字段类型与 postgresql 数据库字段类型的映射
        fieldTypeMap.put(Long.class, new FieldTypeMap(Long.class, "bigint"));
        fieldTypeMap.put(Integer.class, new FieldTypeMap(Integer.class, "integer"));
        fieldTypeMap.put(Short.class, new FieldTypeMap("smallint"));
        fieldTypeMap.put(String.class, new FieldTypeMap(String.class, "varchar(64)"));
        fieldTypeMap.put(LocalDateTime.class, new FieldTypeMap(LocalDateTime.class, "timestamp(3)"));
        fieldTypeMap.put(Boolean.class, new FieldTypeMap(Boolean.class, "boolean"));
        FieldTypeMap list = new FieldTypeMap(List.class, "json");
        fieldTypeMap.put(DBJson.class, new FieldTypeMap("json"));
        fieldTypeMap.put(List.class, list);
        fieldTypeMap.put(Map.class, new FieldTypeMap("json"));
        fieldTypeMap.put(BaseEnum.class, new FieldTypeMap(BaseEnum.class, "integer"));
        fieldTypeMap.put(Enum.class, new FieldTypeMap("integer"));
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
