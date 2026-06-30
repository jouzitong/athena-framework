package org.athena.framework.test.scheduler.service;

import org.athena.framework.test.api.result.TestExecutionResult;
import org.athena.framework.test.api.service.TestService;

/**
 * 默认测试计划调度实现。
 */
public class DefaultTestPlanScheduler implements TestPlanScheduler {

    private final TestService testService;

    public DefaultTestPlanScheduler(TestService testService) {
        this.testService = testService;
    }

    @Override
    public TestExecutionResult dispatch(Long planId, String versionTag) {
        return testService.executePlan(planId, versionTag);
    }
}
