package org.athena.framework.test.api.catalog.dto;

import lombok.Data;

/**
 * 接口响应定义。
 */
@Data
public class ApiCatalogResponseDTO {

    private Integer statusCode;

    private String responseName;

    private String schemaJson;

    private String exampleJson;
}
