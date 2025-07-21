package org.athena.framework.data.jdbc.type;

import org.athena.framework.data.jdbc.utils.JdbcUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhouzhitong
 * @since 2025/7/13
 **/
public enum DbType {

    MYSQL(getMysqlSubTypeMap()),
    POSTGRESQL(getPgSqlSubTypeMap()),
    ORACLE(getOracleSubTypeMap());

    private final Map<Class<?>, String> typeMap;

    DbType(Map<Class<?>, String> typeMap) {
        this.typeMap = typeMap;
    }

    public String getType(Class<?> clazz) {
        if (JdbcUtils.isBaseType(clazz)
                || JdbcUtils.isDateType(clazz)) {
            return typeMap.get(clazz);
        }
        return typeMap.get(Object.class);
    }

    private static Map<Class<?>, String> getMysqlSubTypeMap() {
        Map<Class<?>, String> map = new HashMap<>();
        map.put(String.class, "VARCHAR");
        map.put(Integer.class, "INT");
        map.put(Long.class, "BIGINT");
        map.put(Double.class, "DOUBLE");
        map.put(Float.class, "FLOAT");
        map.put(Boolean.class, "BOOLEAN");
        map.put(Byte.class, "TINYINT");
        map.put(Short.class, "SMALLINT");
        map.put(Byte[].class, "BLOB");
        map.put(java.util.Date.class, "TIMESTAMP");
        map.put(LocalDate.class, "TIMESTAMP");
        map.put(LocalDateTime.class, "TIMESTAMP");
        map.put(Object.class, "json");
        return map;
    }

    private static Map<Class<?>, String> getPgSqlSubTypeMap() {
        Map<Class<?>, String> map = new HashMap<>();
        map.put(String.class, "VARCHAR");
        map.put(Integer.class, "INT");
        map.put(Long.class, "BIGINT");
        map.put(Double.class, "DOUBLE");
        map.put(Float.class, "FLOAT");
        map.put(Boolean.class, "BOOLEAN");
        map.put(Byte.class, "TINYINT");
        map.put(Short.class, "SMALLINT");
        map.put(Byte[].class, "BLOB");
        map.put(java.util.Date.class, "TIMESTAMP");
        map.put(LocalDate.class, "TIMESTAMP");
        map.put(LocalDateTime.class, "TIMESTAMP");
        map.put(Object.class, "json");
        return map;
    }

    private static Map<Class<?>, String> getOracleSubTypeMap() {
        Map<Class<?>, String> map = new HashMap<>();
        map.put(String.class, "VARCHAR2");
        map.put(Integer.class, "NUMBER");
        map.put(Long.class, "NUMBER");
        map.put(Double.class, "NUMBER");
        map.put(Float.class, "NUMBER");
        map.put(Boolean.class, "NUMBER");
        map.put(Byte.class, "NUMBER");
        map.put(Short.class, "NUMBER");
        map.put(Byte[].class, "BLOB");
        map.put(java.util.Date.class, "TIMESTAMP");
        map.put(LocalDate.class, "TIMESTAMP");
        map.put(LocalDateTime.class, "TIMESTAMP");
        map.put(Object.class, "json");
        return map;
    }

}
