package org.athena.framework.test.service;

import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.athena.framework.test.api.enums.TestStepType;
import org.athena.framework.test.api.service.StepExecutor;

/**
 * 步骤执行器注册表。
 */
public class DefaultStepExecutorRegistry {

    private final Map<TestStepType, StepExecutor> executorMap = new EnumMap<>(TestStepType.class);

    public DefaultStepExecutorRegistry(List<StepExecutor> executors) {
        for (StepExecutor executor : executors) {
            executorMap.put(executor.stepType(), executor);
        }
    }

    public StepExecutor get(TestStepType stepType) {
        return executorMap.get(stepType);
    }

    public Collection<StepExecutor> all() {
        return executorMap.values();
    }
}
