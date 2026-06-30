package org.athena.framework.test.api.constant;

import org.arthena.framework.common.constant.ErrCodeConstant;

/**
 * 测试模块错误码别名。
 */
public interface TestErrorCode {

    Integer TEST_CONFIG_ERROR = ErrCodeConstant.TEST_CONFIG_ERROR;

    Integer TEST_STEP_EXECUTOR_NOT_FOUND = ErrCodeConstant.TEST_STEP_EXECUTOR_NOT_FOUND;

    Integer TEST_ASSERTION_FAILED = ErrCodeConstant.TEST_ASSERTION_FAILED;

    Integer TEST_VERSION_NOT_FOUND = ErrCodeConstant.TEST_VERSION_NOT_FOUND;

    Integer TEST_PLAN_EXECUTION_FAILED = ErrCodeConstant.TEST_PLAN_EXECUTION_FAILED;
}
