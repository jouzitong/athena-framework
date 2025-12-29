package org.athena.framework.data.mybatis.bean.meta;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 表约束定义
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConstraintMeta {

    /**
     * 约束名称
     */
    private String name;

    /**
     * 约束类型
     */
    private String type;

    /**
     * 涉及的列名
     */
    private List<String> columnNames;


}
