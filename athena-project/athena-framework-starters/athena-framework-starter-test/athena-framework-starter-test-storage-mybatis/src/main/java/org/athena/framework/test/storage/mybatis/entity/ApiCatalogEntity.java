package org.athena.framework.test.storage.mybatis.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;
import org.athena.framework.data.mybatis.annotations.DdlColumnLength;
import org.athena.framework.data.mybatis.entity.LogicalDeleteEntity;

/**
 * 接口资产主定义实体。
 */
@Getter
@Setter
@TableName("athena_test_api_catalog")
public class ApiCatalogEntity extends LogicalDeleteEntity {

    @TableField("service_name")
    @DdlColumnLength(64)
    private String serviceName;

    @TableField("module_name")
    @DdlColumnLength(64)
    private String moduleName;

    @TableField("api_code")
    @DdlColumnLength(128)
    private String apiCode;

    @TableField("api_name")
    @DdlColumnLength(128)
    private String apiName;

    @TableField("protocol")
    @DdlColumnLength(32)
    private String protocol;

    @TableField("http_method")
    @DdlColumnLength(16)
    private String httpMethod;

    @TableField("path")
    @DdlColumnLength(255)
    private String path;

    @TableField("auth_type")
    @DdlColumnLength(64)
    private String authType;

    @TableField("content_type")
    @DdlColumnLength(64)
    private String contentType;

    @TableField("status")
    @DdlColumnLength(32)
    private String status;

    @TableField("current_version")
    @DdlColumnLength(64)
    private String currentVersion;

    @TableField("description")
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
}
