package org.athena.framework.test.storage.mybatis.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;
import org.athena.framework.data.mybatis.annotations.DdlColumnLength;
import org.athena.framework.data.mybatis.entity.LogicalDeleteEntity;

/**
 * 测试节点引用接口资产关系实体。
 */
@Getter
@Setter
@TableName("athena_test_flow_node_api_ref")
public class TestFlowNodeApiRefEntity extends LogicalDeleteEntity {

    @TableField("node_code")
    @DdlColumnLength(64)
    private String nodeCode;

    @TableField("api_catalog_id")
    private Long apiCatalogId;

    @TableField("api_catalog_version_id")
    private Long apiCatalogVersionId;

    @TableField("ref_mode")
    @DdlColumnLength(32)
    private String refMode;

    @TableField("override_config_json")
    @Column(name = "override_config_json", columnDefinition = "LONGTEXT")
    private String overrideConfigJson;
}
