package org.athena.framework.test.api.service;

import org.athena.framework.test.api.model.TestAssertionDefinition;
import org.athena.framework.test.api.model.TestExecutionContext;
import org.athena.framework.test.api.model.TestStepDefinition;
import org.athena.framework.test.api.result.TestStepExecutionResult;

/**
 * 断言评估器。
 */
public interface AssertionEvaluator {

    boolean supports(String assertionType);

    void evaluate(TestExecutionContext context,
                  TestStepDefinition step,
                  TestAssertionDefinition assertion,
                  TestStepExecutionResult result);
}
