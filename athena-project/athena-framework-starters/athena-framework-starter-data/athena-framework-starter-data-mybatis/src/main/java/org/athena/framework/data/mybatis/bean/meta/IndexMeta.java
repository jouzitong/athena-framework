package org.athena.framework.data.mybatis.bean.meta;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 表索引描述定义
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IndexMeta {

    /**
     * 索引名称
     */
    private String name;

    /**
     * 索引类型
     */
    private String type;

    /**
     * 索引关联的列名
     */
    private List<String> columnNames;

    /**
     * 是否唯一索引
     */
    private boolean unique;

}
