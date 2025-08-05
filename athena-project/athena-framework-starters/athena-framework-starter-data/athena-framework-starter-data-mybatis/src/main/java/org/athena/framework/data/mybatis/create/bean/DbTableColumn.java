package org.athena.framework.data.mybatis.create.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhouzhitong
 * @since 2025/7/17
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DbTableColumn {

    /**
     * 列名
     */
    private String columnName;

    /**
     * 数据库类型
     */
    private String type;

    /**
     * 长度
     */
    private Integer length;

    /**
     * 小数位数
     */
    private Integer scale;

    /**
     * 是否为空
     */
    private Boolean nullable;

    /**
     * 默认值
     */
    private String defaultValue;

}
