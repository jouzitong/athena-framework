package org.athena.framework.test.websocket.executor;

import org.athena.framework.test.api.enums.TestExecutionStatus;
import org.athena.framework.test.api.enums.TestStepType;
import org.athena.framework.test.api.model.TestExecutionContext;
import org.athena.framework.test.api.model.TestStepDefinition;
import org.athena.framework.test.api.result.TestStepExecutionResult;
import org.athena.framework.test.api.service.StepExecutor;

/**
 * WebSocket 步骤执行骨架。
 */
public class WebSocketStepExecutor implements StepExecutor {

    @Override
    public TestStepType stepType() {
        return TestStepType.WEBSOCKET;
    }

    @Override
    public TestStepExecutionResult execute(TestExecutionContext context, TestStepDefinition step) {
        TestStepExecutionResult result = new TestStepExecutionResult();
        result.setStatus(TestExecutionStatus.SKIPPED);
        result.setSuccess(Boolean.FALSE);
        result.setRequestPayload(step.getConfigJson());
        result.setResponsePayload("{\"message\":\"websocket test executor skeleton\"}");
        return result;
    }
}
