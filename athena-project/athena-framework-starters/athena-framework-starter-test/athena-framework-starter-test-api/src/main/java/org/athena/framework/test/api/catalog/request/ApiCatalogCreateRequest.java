package org.athena.framework.test.api.catalog.request;

import lombok.Data;

/**
 * 创建接口资产请求。
 */
@Data
public class ApiCatalogCreateRequest {

    private String serviceName;

    private String moduleName;

    private String apiCode;

    private String apiName;

    private String protocol;

    private String httpMethod;

    private String path;

    private String authType;

    private String contentType;

    private String description;
}
