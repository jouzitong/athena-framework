package org.athena.framework.test.api.model;

import lombok.Data;

/**
 * 断言定义。
 */
@Data
public class TestAssertionDefinition {

    private String type;

    private String expression;

    private String expectedValue;

    private String message;
}
