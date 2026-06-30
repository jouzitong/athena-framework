package org.athena.framework.test.api.catalog.dto;

import java.time.LocalDateTime;
import lombok.Data;

/**
 * 接口资产摘要。
 */
@Data
public class ApiCatalogDTO {

    private Long id;

    private String serviceName;

    private String moduleName;

    private String apiCode;

    private String apiName;

    private String httpMethod;

    private String path;

    private String status;

    private String currentVersion;

    private LocalDateTime createdAt;
}
