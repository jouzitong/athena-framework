package org.athena.framework.test.api.service;

import java.util.Optional;
import org.athena.framework.test.api.request.TestExecuteRequest;

/**
 * 测试计划读取接口。
 */
public interface TestPlanRepository {

    Optional<TestExecuteRequest> findExecuteRequest(Long planId, String versionTag);
}
