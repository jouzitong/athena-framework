package org.athena.framework.test.api.service;

import org.athena.framework.test.api.request.TestExecuteRequest;
import org.athena.framework.test.api.result.TestExecutionResult;

/**
 * 测试统一入口。
 */
public interface TestService {

    TestExecutionResult executeScene(TestExecuteRequest request);

    TestExecutionResult executePlan(Long planId, String versionTag);
}
