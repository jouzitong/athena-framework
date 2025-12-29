package org.athena.framework.data.mybatis.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.athena.framework.data.mybatis.bean.meta.ColumnMeta;
import org.athena.framework.data.mybatis.bean.meta.IndexMeta;

import java.util.List;

/**
 * 表描述定义
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TableMeta {

    /**
     * 表名称
     */
    private String name;

    /**
     * 表说明
     */
    private String comment;

    /**
     * 表字段列表
     */
    private List<ColumnMeta> columns;

    /**
     * 表索引列表
     */
    private List<IndexMeta> indexes;



}
