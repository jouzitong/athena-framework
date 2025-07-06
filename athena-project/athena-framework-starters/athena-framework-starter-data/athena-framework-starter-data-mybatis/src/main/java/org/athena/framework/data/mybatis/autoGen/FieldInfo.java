package org.athena.framework.data.mybatis.autoGen;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhouzhitong
 * @since 2024-03-24
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FieldInfo {

    /**
     * 字段名
     */
    private String name;

    /**
     * 字段描述
     */
    private String comment;

    /**
     * 数据类型
     */
    private String type;

    private String defaultValue;

    private boolean nullable = true;

}
