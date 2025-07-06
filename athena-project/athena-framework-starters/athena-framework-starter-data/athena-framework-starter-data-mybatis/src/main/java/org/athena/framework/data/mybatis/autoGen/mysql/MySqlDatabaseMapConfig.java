package org.athena.framework.data.mybatis.autoGen.mysql;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.Getter;
import org.arthena.framework.common.base.IEnum;
import org.athena.framework.data.mybatis.autoGen.DatabaseTypeMapConfig;
import org.athena.framework.data.mybatis.properties.DefaultMapperProperties;
import org.athena.framework.data.mybatis.properties.bean.FieldTypeMap;
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
public class MySqlDatabaseMapConfig implements DatabaseTypeMapConfig {

    /**
     * 字段类型映射
     */
    @Getter
    private final Map<Class<?>, FieldTypeMap> fieldTypeMap;

    @Resource
    private DefaultMapperProperties mapperProperties;

    public MySqlDatabaseMapConfig() {
        this.fieldTypeMap = new HashMap<>();
        init();
    }

    @PostConstruct
    public void loadTypeMap(){
        List<FieldTypeMap> fieldTypeMaps = mapperProperties.getFieldTypeMaps();
        if (fieldTypeMaps==null){
            return;
        }
        for (FieldTypeMap typeMap : fieldTypeMaps) {
            fieldTypeMap.put(typeMap.getFieldType(), typeMap);
        }
    }

    private void init() {
        // 初始化 常见的Bean实体字段类型与 mysql 数据库字段类型的映射
        fieldTypeMap.put(Long.class, new FieldTypeMap(Long.class, "bigint"));
        fieldTypeMap.put(Integer.class, new FieldTypeMap(Integer.class, "int"));
        fieldTypeMap.put(Short.class, new FieldTypeMap("smallint"));
        fieldTypeMap.put(String.class, new FieldTypeMap(String.class, "varchar(64)"));
        fieldTypeMap.put(LocalDateTime.class, new FieldTypeMap(LocalDateTime.class, "datetime"));
        fieldTypeMap.put(Boolean.class, new FieldTypeMap(Boolean.class, "tinyint"));
        FieldTypeMap list = new FieldTypeMap(List.class, "json");
        list.add(Long.class, "json");
        list.add(Integer.class, "json");
        list.add(String.class, "json");
        fieldTypeMap.put(List.class, list);
        fieldTypeMap.put(Object.class, new FieldTypeMap("json"));
        fieldTypeMap.put(IEnum.class, new FieldTypeMap(IEnum.class, "int"));
        fieldTypeMap.put(Enum.class, new FieldTypeMap(IEnum.class, "int"));
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
