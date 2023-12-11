package com.zhouzhitong.lib.mapper.base;

import lombok.Getter;

import java.io.Serializable;

/**
 * 排序参数
 *
 * @author zhouzhitong
 * @since 2022/9/25
 */
@Getter
public class Sort implements Serializable {

    /**
     * 正序
     */
    public static final String DESC = "desc";

    /**
     * 逆序
     */
    public static final String ASC = "asc";

    /**
     * 字段名
     */
    private final String column;

    /**
     * 正序: desc
     * <p>
     * 逆序: asc
     */
    private final String sort;

    private Sort(String column, String sort) {
        this.column = column;
        this.sort = sort;
    }

    public static Sort of(String column, String sort) {
        return new Sort(column, sort);
    }

    /**
     * 逆序
     *
     * @param field
     * @return
     */
    public static Sort desc(String field) {
        return new Sort(field, DESC);
    }

    /**
     * 逆序
     *
     * @param field
     * @return
     */
    public static Sort asc(String field) {
        return new Sort(field, ASC);
    }

    public boolean isAsc() {
        return ASC.equals(sort);
    }

}
