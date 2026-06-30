package org.athena.framework.test.api.catalog.dto;

import lombok.Data;

/**
 * 接口参数定义。
 */
@Data
public class ApiCatalogParameterDTO {

    private String paramName;

    private String paramIn;

    private String dataType;

    private Boolean required = Boolean.FALSE;

    private String defaultValue;

    private String exampleValue;

    private String description;
}
