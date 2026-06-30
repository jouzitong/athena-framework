package org.athena.framework.test.api.service;

import org.athena.framework.test.api.enums.TestStepType;
import org.athena.framework.test.api.model.TestExecutionContext;
import org.athena.framework.test.api.model.TestStepDefinition;
import org.athena.framework.test.api.result.TestStepExecutionResult;

/**
 * 测试步骤执行器。
 */
public interface StepExecutor {

    TestStepType stepType();

    TestStepExecutionResult execute(TestExecutionContext context, TestStepDefinition step);
}
