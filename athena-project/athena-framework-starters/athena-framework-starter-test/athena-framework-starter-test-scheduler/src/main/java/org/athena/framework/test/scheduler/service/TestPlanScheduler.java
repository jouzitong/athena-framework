package org.athena.framework.test.scheduler.service;

import org.athena.framework.test.api.result.TestExecutionResult;

/**
 * 测试计划调度入口。
 */
public interface TestPlanScheduler {

    TestExecutionResult dispatch(Long planId, String versionTag);
}
