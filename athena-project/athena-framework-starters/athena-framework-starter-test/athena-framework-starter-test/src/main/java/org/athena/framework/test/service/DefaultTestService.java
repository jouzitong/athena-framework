package org.athena.framework.test.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import org.athena.framework.test.api.enums.TestExecutionStatus;
import org.athena.framework.test.api.model.TestAssertionDefinition;
import org.athena.framework.test.api.model.TestExecutionContext;
import org.athena.framework.test.api.model.TestSceneDefinition;
import org.athena.framework.test.api.model.TestStepDefinition;
import org.athena.framework.test.api.request.TestExecuteRequest;
import org.athena.framework.test.api.result.TestExecutionResult;
import org.athena.framework.test.api.result.TestStepExecutionResult;
import org.athena.framework.test.api.service.AssertionEvaluator;
import org.athena.framework.test.api.service.StepExecutor;
import org.athena.framework.test.api.service.TestExecutionRepository;
import org.athena.framework.test.api.service.TestPlanRepository;
import org.athena.framework.test.api.service.TestSceneDefinitionRepository;
import org.athena.framework.test.api.service.TestService;
import org.athena.framework.test.config.AthenaTestProperties;

/**
 * 默认测试执行服务。
 */
public class DefaultTestService implements TestService {

    private final DefaultStepExecutorRegistry stepExecutorRegistry;
    private final AthenaTestProperties properties;
    private final List<AssertionEvaluator> assertionEvaluators;
    private final TestSceneDefinitionRepository sceneDefinitionRepository;
    private final TestPlanRepository planRepository;
    private final TestExecutionRepository executionRepository;

    public DefaultTestService(DefaultStepExecutorRegistry stepExecutorRegistry,
                              AthenaTestProperties properties,
                              List<AssertionEvaluator> assertionEvaluators,
                              TestSceneDefinitionRepository sceneDefinitionRepository,
                              TestPlanRepository planRepository,
                              TestExecutionRepository executionRepository) {
        this.stepExecutorRegistry = stepExecutorRegistry;
        this.properties = properties;
        this.assertionEvaluators = assertionEvaluators;
        this.sceneDefinitionRepository = sceneDefinitionRepository;
        this.planRepository = planRepository;
        this.executionRepository = executionRepository;
    }

    @Override
    public TestExecutionResult executeScene(TestExecuteRequest request) {
        Objects.requireNonNull(request, "request can not be null");
        TestSceneDefinition sceneDefinition = resolveSceneDefinition(request);
        TestExecutionContext context = buildContext(request, sceneDefinition);
        TestExecutionResult result = new TestExecutionResult();
        result.setSceneId(sceneDefinition.getSceneId());
        result.setSceneCode(sceneDefinition.getSceneCode());
        result.setVersionTag(sceneDefinition.getVersionTag());
        result.setPlanId(request.getPlanId());
        result.setTriggerType(request.getTriggerType());
        result.setStartedAt(context.getStartedAt());
        result.setStatus(TestExecutionStatus.RUNNING);
        executeSteps(sceneDefinition, context, result);
        result.setFinishedAt(LocalDateTime.now());
        result.setDurationMs(Duration.between(result.getStartedAt(), result.getFinishedAt()).toMillis());
        result.setStatus(resolveExecutionStatus(result.getStepResults()));
        result.setSuccess(TestExecutionStatus.SUCCESS == result.getStatus());
        result.setSummary(buildSummary(result.getStepResults(), result.getStatus()));
        if (executionRepository != null) {
            result.setExecutionId(executionRepository.saveExecution(result));
        }
        return result;
    }

    @Override
    public TestExecutionResult executePlan(Long planId, String versionTag) {
        if (planRepository == null) {
            throw new IllegalStateException("TestPlanRepository is not configured");
        }
        TestExecuteRequest request = planRepository.findExecuteRequest(planId, versionTag)
            .orElseThrow(() -> new IllegalStateException("test plan not found: " + planId));
        return executeScene(request);
    }

    private void executeSteps(TestSceneDefinition sceneDefinition,
                              TestExecutionContext context,
                              TestExecutionResult result) {
        for (TestStepDefinition step : sceneDefinition.getSteps()) {
            TestStepExecutionResult stepResult = executeSingleStep(context, step);
            result.getStepResults().add(stepResult);
            context.getStepResults().add(stepResult);
            context.getVariables().putAll(stepResult.getExtractedVariables());
            if (stepResult.getStatus() == TestExecutionStatus.FAILED
                && (properties.isFailFast() || !Boolean.TRUE.equals(step.getContinueOnFailure()))) {
                break;
            }
        }
    }

    private TestStepExecutionResult executeSingleStep(TestExecutionContext context, TestStepDefinition step) {
        StepExecutor executor = stepExecutorRegistry.get(step.getStepType());
        if (executor == null) {
            return failedStep(step, "step executor not found: " + step.getStepType());
        }
        try {
            TestStepExecutionResult result = executor.execute(context, step);
            result.setStepCode(step.getStepCode());
            result.setStepName(step.getName());
            result.setStepOrder(step.getStepOrder());
            result.setStepType(step.getStepType());
            evaluateAssertions(context, step, result);
            return result;
        } catch (Exception ex) {
            return failedStep(step, ex.getMessage());
        }
    }

    private void evaluateAssertions(TestExecutionContext context,
                                    TestStepDefinition step,
                                    TestStepExecutionResult result) {
        if (step.getAssertions() == null || step.getAssertions().isEmpty()) {
            return;
        }
        for (TestAssertionDefinition assertion : step.getAssertions()) {
            for (AssertionEvaluator evaluator : assertionEvaluators) {
                if (evaluator.supports(assertion.getType())) {
                    evaluator.evaluate(context, step, assertion, result);
                    return;
                }
            }
        }
    }

    private TestExecutionContext buildContext(TestExecuteRequest request, TestSceneDefinition sceneDefinition) {
        TestExecutionContext context = new TestExecutionContext();
        context.setSceneId(sceneDefinition.getSceneId());
        context.setSceneCode(sceneDefinition.getSceneCode());
        context.setVersionTag(sceneDefinition.getVersionTag());
        context.setTriggerType(request.getTriggerType());
        context.setOperatorId(request.getOperatorId());
        context.setStartedAt(LocalDateTime.now());
        if (sceneDefinition.getVariables() != null) {
            context.getVariables().putAll(sceneDefinition.getVariables());
        }
        if (request.getVariables() != null) {
            context.getVariables().putAll(request.getVariables());
        }
        return context;
    }

    private TestSceneDefinition resolveSceneDefinition(TestExecuteRequest request) {
        if (sceneDefinitionRepository == null) {
            throw new IllegalStateException("TestSceneDefinitionRepository is not configured");
        }
        if (request.getSceneId() != null) {
            return sceneDefinitionRepository.findBySceneIdAndVersion(request.getSceneId(), request.getVersionTag())
                .orElseThrow(() -> new IllegalStateException("test scene not found: " + request.getSceneId()));
        }
        if (StringUtils.isNotBlank(request.getSceneCode())) {
            return sceneDefinitionRepository.findBySceneCodeAndVersion(request.getSceneCode(), request.getVersionTag())
                .orElseThrow(() -> new IllegalStateException("test scene not found: " + request.getSceneCode()));
        }
        throw new IllegalArgumentException("sceneId or sceneCode is required");
    }

    private TestStepExecutionResult failedStep(TestStepDefinition step, String errorMessage) {
        TestStepExecutionResult result = new TestStepExecutionResult();
        result.setStepCode(step.getStepCode());
        result.setStepName(step.getName());
        result.setStepOrder(step.getStepOrder());
        result.setStepType(step.getStepType());
        result.setStatus(TestExecutionStatus.FAILED);
        result.setSuccess(Boolean.FALSE);
        result.setErrorMessage(errorMessage);
        return result;
    }

    private TestExecutionStatus resolveExecutionStatus(List<TestStepExecutionResult> stepResults) {
        boolean hasSuccess = false;
        boolean hasSkipped = false;
        for (TestStepExecutionResult stepResult : stepResults) {
            if (stepResult.getStatus() == TestExecutionStatus.FAILED) {
                return TestExecutionStatus.FAILED;
            }
            if (stepResult.getStatus() == TestExecutionStatus.SUCCESS) {
                hasSuccess = true;
            }
            if (stepResult.getStatus() == TestExecutionStatus.SKIPPED) {
                hasSkipped = true;
            }
        }
        if (hasSuccess) {
            return TestExecutionStatus.SUCCESS;
        }
        if (hasSkipped) {
            return TestExecutionStatus.SKIPPED;
        }
        return TestExecutionStatus.PENDING;
    }

    private String buildSummary(List<TestStepExecutionResult> stepResults, TestExecutionStatus status) {
        long successCount = stepResults.stream()
            .filter(item -> item.getStatus() == TestExecutionStatus.SUCCESS)
            .count();
        long failedCount = stepResults.stream()
            .filter(item -> item.getStatus() == TestExecutionStatus.FAILED)
            .count();
        long skippedCount = stepResults.stream()
            .filter(item -> item.getStatus() == TestExecutionStatus.SKIPPED)
            .count();
        return "status=" + status
            + ", successSteps=" + successCount
            + ", failedSteps=" + failedCount
            + ", skippedSteps=" + skippedCount;
    }
}
