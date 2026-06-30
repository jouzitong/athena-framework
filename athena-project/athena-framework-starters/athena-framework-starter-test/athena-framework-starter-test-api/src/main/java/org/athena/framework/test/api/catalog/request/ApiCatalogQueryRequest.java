package org.athena.framework.test.api.catalog.request;

import lombok.Data;

/**
 * 查询接口资产请求。
 */
@Data
public class ApiCatalogQueryRequest {

    private String serviceName;

    private String moduleName;

    private String keyword;

    private String httpMethod;

    private String status;
}
