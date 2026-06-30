package org.athena.framework.test.http.executor;

import org.athena.framework.test.api.enums.TestExecutionStatus;
import org.athena.framework.test.api.enums.TestStepType;
import org.athena.framework.test.api.model.TestExecutionContext;
import org.athena.framework.test.api.model.TestStepDefinition;
import org.athena.framework.test.api.result.TestStepExecutionResult;
import org.athena.framework.test.api.service.StepExecutor;

/**
 * HTTP 步骤执行骨架。
 */
public class HttpStepExecutor implements StepExecutor {

    @Override
    public TestStepType stepType() {
        return TestStepType.HTTP;
    }

    @Override
    public TestStepExecutionResult execute(TestExecutionContext context, TestStepDefinition step) {
        TestStepExecutionResult result = new TestStepExecutionResult();
        result.setStatus(TestExecutionStatus.SKIPPED);
        result.setSuccess(Boolean.FALSE);
        result.setRequestPayload(step.getConfigJson());
        result.setResponsePayload("{\"message\":\"http test executor skeleton\"}");
        return result;
    }
}
