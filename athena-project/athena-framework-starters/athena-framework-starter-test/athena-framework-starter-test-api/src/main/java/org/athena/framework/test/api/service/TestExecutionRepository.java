package org.athena.framework.test.api.service;

import org.athena.framework.test.api.result.TestExecutionResult;

/**
 * 执行结果持久化接口。
 */
public interface TestExecutionRepository {

    Long saveExecution(TestExecutionResult result);
}
