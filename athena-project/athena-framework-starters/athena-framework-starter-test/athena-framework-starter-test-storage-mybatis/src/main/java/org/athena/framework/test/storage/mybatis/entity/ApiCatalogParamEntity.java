package org.athena.framework.test.storage.mybatis.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;
import org.athena.framework.data.mybatis.annotations.DdlColumnLength;
import org.athena.framework.data.mybatis.entity.LogicalDeleteEntity;

/**
 * 接口参数定义实体。
 */
@Getter
@Setter
@TableName("athena_test_api_catalog_param")
public class ApiCatalogParamEntity extends LogicalDeleteEntity {

    @TableField("api_catalog_version_id")
    private Long apiCatalogVersionId;

    @TableField("param_name")
    @DdlColumnLength(128)
    private String paramName;

    @TableField("param_in")
    @DdlColumnLength(32)
    private String paramIn;

    @TableField("data_type")
    @DdlColumnLength(32)
    private String dataType;

    @TableField("required_flag")
    private Integer requiredFlag;

    @TableField("default_value")
    @DdlColumnLength(255)
    private String defaultValue;

    @TableField("example_value")
    @DdlColumnLength(255)
    private String exampleValue;

    @TableField("description")
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
}
