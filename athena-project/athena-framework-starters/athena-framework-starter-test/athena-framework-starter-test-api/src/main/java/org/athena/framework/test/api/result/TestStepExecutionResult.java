package org.athena.framework.test.api.result;

import java.util.LinkedHashMap;
import java.util.Map;
import lombok.Data;
import org.athena.framework.test.api.enums.TestExecutionStatus;
import org.athena.framework.test.api.enums.TestStepType;

/**
 * 单步执行结果。
 */
@Data
public class TestStepExecutionResult {

    private String stepCode;

    private String stepName;

    private Integer stepOrder;

    private TestStepType stepType;

    private TestExecutionStatus status = TestExecutionStatus.PENDING;

    private Boolean success = Boolean.FALSE;

    private Long durationMs = 0L;

    private String requestPayload;

    private String responsePayload;

    private String errorMessage;

    private Map<String, Object> extractedVariables = new LinkedHashMap<>();
}
