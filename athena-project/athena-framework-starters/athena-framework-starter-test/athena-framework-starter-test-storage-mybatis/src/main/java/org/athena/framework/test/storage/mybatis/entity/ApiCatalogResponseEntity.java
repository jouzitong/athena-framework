package org.athena.framework.test.storage.mybatis.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;
import org.athena.framework.data.mybatis.annotations.DdlColumnLength;
import org.athena.framework.data.mybatis.entity.LogicalDeleteEntity;

/**
 * 接口响应定义实体。
 */
@Getter
@Setter
@TableName("athena_test_api_catalog_response")
public class ApiCatalogResponseEntity extends LogicalDeleteEntity {

    @TableField("api_catalog_version_id")
    private Long apiCatalogVersionId;

    @TableField("status_code")
    private Integer statusCode;

    @TableField("response_name")
    @DdlColumnLength(128)
    private String responseName;

    @TableField("schema_json")
    @Column(name = "schema_json", columnDefinition = "LONGTEXT")
    private String schemaJson;

    @TableField("example_json")
    @Column(name = "example_json", columnDefinition = "LONGTEXT")
    private String exampleJson;
}
