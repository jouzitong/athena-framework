package org.athena.framework.test.api.catalog.request;

import lombok.Data;

/**
 * 发布接口资产版本请求。
 */
@Data
public class ApiCatalogPublishRequest {

    private Long apiCatalogId;

    private String versionTag;

    private String versionStatus;

    private String changeComment;

    private Long publishedBy;

    private String definitionJson;
}
