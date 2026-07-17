package org.athena.framework.data.jdbc.annotations;

/**
 * 数据库索引类型。
 *
 * @author zhouzhitong
 * @since 2026/7/17
 */
public enum JdbcIndexType {

    /**
     * 普通索引。
     */
    INDEX("idx"),

    /**
     * 唯一索引。
     */
    UNIQUE("uk"),

    /**
     * 全文索引。
     */
    FULLTEXT("ft"),

    /**
     * 空间索引。
     */
    SPATIAL("sp");

    /**
     * 用于自动生成索引名的类型标识。
     */
    private final String code;

    JdbcIndexType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
