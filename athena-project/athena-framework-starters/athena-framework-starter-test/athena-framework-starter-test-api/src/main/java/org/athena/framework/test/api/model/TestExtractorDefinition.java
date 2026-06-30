package org.athena.framework.test.api.model;

import lombok.Data;

/**
 * 变量提取定义。
 */
@Data
public class TestExtractorDefinition {

    private String name;

    private String expression;

    private String defaultValue;

    private Boolean required = Boolean.FALSE;
}
