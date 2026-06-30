package org.athena.framework.test.api.catalog.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 * 接口资产完整定义。
 */
@Data
public class ApiCatalogDefinitionDTO {

    private Long apiCatalogId;

    private Long apiCatalogVersionId;

    private String serviceName;

    private String moduleName;

    private String apiCode;

    private String apiName;

    private String protocol;

    private String httpMethod;

    private String path;

    private String authType;

    private String contentType;

    private String bodyExample;

    private String schemaJson;

    private String versionTag;

    private List<ApiCatalogParameterDTO> parameters = new ArrayList<>();

    private List<ApiCatalogResponseDTO> responses = new ArrayList<>();
}
