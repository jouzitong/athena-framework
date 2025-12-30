package org.athena.framework.data.mybatis.bean.meta;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 表字段描述定义
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ColumnMeta {

    /**
     * 列名. 纯净的字符, 不要带 `` 等关键符号
     */
    private String name;

    /**
     * 数据类型
     */
    private String dataType;

    /**
     * java 类型
     */
    private Class<?> javaType;

    /**
     * 长度
     */
    private int length;

    /**
     * 小数位数
     */
    private Integer scale;

    /**
     * 是否可为空
     */
    private boolean nullable;

    /**
     * 是否为主键
     */
    private boolean primaryKey;

    /**
     * 是否自增
     */
    private boolean autoIncrement;

    /**
     * 默认值
     */
    private String defaultValue;

    /**
     * 字段注释
     */
    private String comment;

}
