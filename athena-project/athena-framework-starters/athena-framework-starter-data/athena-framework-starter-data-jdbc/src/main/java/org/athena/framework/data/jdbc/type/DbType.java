package org.athena.framework.data.jdbc.type;

import org.arthena.framework.common.enums.IEnum;
import org.arthena.framework.common.utils.ObjectUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhouzhitong
 * @since 2025/7/13
 **/
public enum DbType {

    MYSQL(getMysqlSubTypeMap()),
    POSTGRESQL(getPgSqlSubTypeMap()),
    ORACLE(getOracleSubTypeMap());

    private final Map<Class<?>, List<String>> typeMap;

    DbType(Map<Class<?>, List<String>> typeMap) {
        this.typeMap = typeMap;
    }

    public String getType(Class<?> clazz) {
        if (ObjectUtils.isBaseType(clazz)
                || ObjectUtils.isDateType(clazz)) {
            return getPreferredType(clazz);
        }
        if (IEnum.class.isAssignableFrom(clazz) || clazz.isEnum()) {
            return getPreferredType(IEnum.class);
        }
        return getPreferredType(Object.class);
    }

    public String getType(Class<?> clazz, int length) {
        if (ObjectUtils.isBaseType(clazz)
                || ObjectUtils.isDateType(clazz)) {
            String s = getPreferredType(clazz);
            if (s == null) {
                return getPreferredType(Object.class);
            }
            if (s.equals("VARCHAR")) {
                int varcharLength = length > 0 ? length : 255;
                return s + "(" + varcharLength + ")";
            }
            return s;
        }
        if (IEnum.class.isAssignableFrom(clazz) || clazz.isEnum()) {
            return getPreferredType(IEnum.class);
        }
        return getPreferredType(Object.class);
    }

    /**
     * 判断两个数据库类型是否是当前数据库方言下的等价类型。
     */
    public boolean isEquivalentType(String firstType, String secondType) {
        if (firstType == null || secondType == null) {
            return false;
        }
        if (firstType.equalsIgnoreCase(secondType)) {
            return true;
        }
        return typeMap.values().stream()
                .anyMatch(types -> containsIgnoreCase(types, firstType) && containsIgnoreCase(types, secondType));
    }

    private String getPreferredType(Class<?> clazz) {
        List<String> types = typeMap.get(clazz);
        return types == null || types.isEmpty() ? null : types.get(0);
    }

    private static boolean containsIgnoreCase(List<String> types, String type) {
        return types.stream().anyMatch(candidate -> candidate.equalsIgnoreCase(type));
    }

    private static Map<Class<?>, List<String>> getMysqlSubTypeMap() {
        Map<Class<?>, List<String>> map = new HashMap<>();
        putType(map, String.class, "VARCHAR");
        putType(map, Integer.class, "INT");
        putType(map, Long.class, "BIGINT");
        putType(map, Double.class, "DOUBLE");
        putType(map, Float.class, "FLOAT");
        putType(map, Boolean.class, "BOOLEAN", "TINYINT", "tinyint");
        putType(map, boolean.class, "BOOLEAN", "TINYINT", "tinyint");
        putType(map, Byte.class, "TINYINT");
        putType(map, Short.class, "SMALLINT");
        putType(map, IEnum.class, "INT");
        putType(map, Byte[].class, "BLOB");
        putType(map, java.util.Date.class, "TIMESTAMP");
        putType(map, LocalDate.class, "TIMESTAMP");
        putType(map, LocalDateTime.class, "TIMESTAMP");
        putType(map, Object.class, "json");
        return map;
    }

    private static Map<Class<?>, List<String>> getPgSqlSubTypeMap() {
        Map<Class<?>, List<String>> map = new HashMap<>();
        putType(map, String.class, "VARCHAR");
        putType(map, Integer.class, "INT");
        putType(map, Long.class, "BIGINT");
        putType(map, Double.class, "DOUBLE");
        putType(map, Float.class, "FLOAT");
        putType(map, Boolean.class, "BOOLEAN");
        putType(map, Byte.class, "TINYINT");
        putType(map, Short.class, "SMALLINT");
        putType(map, IEnum.class, "INT");
        putType(map, Byte[].class, "BLOB");
        putType(map, java.util.Date.class, "TIMESTAMP");
        putType(map, LocalDate.class, "TIMESTAMP");
        putType(map, LocalDateTime.class, "TIMESTAMP");
        putType(map, Object.class, "json");
        return map;
    }

    private static Map<Class<?>, List<String>> getOracleSubTypeMap() {
        Map<Class<?>, List<String>> map = new HashMap<>();
        putType(map, String.class, "VARCHAR2");
        putType(map, Integer.class, "NUMBER");
        putType(map, Long.class, "NUMBER");
        putType(map, Double.class, "NUMBER");
        putType(map, Float.class, "NUMBER");
        putType(map, Boolean.class, "NUMBER");
        putType(map, Byte.class, "NUMBER");
        putType(map, Short.class, "NUMBER");
        putType(map, IEnum.class, "NUMBER");
        putType(map, Byte[].class, "BLOB");
        putType(map, java.util.Date.class, "TIMESTAMP");
        putType(map, LocalDate.class, "TIMESTAMP");
        putType(map, LocalDateTime.class, "TIMESTAMP");
        putType(map, Object.class, "json");
        return map;
    }

    private static void putType(Map<Class<?>, List<String>> map, Class<?> javaType, String... types) {
        map.put(javaType, Arrays.asList(types));
    }

}
