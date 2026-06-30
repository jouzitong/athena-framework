package org.athena.framework.test.config;

import java.util.List;
import org.athena.framework.test.api.service.AssertionEvaluator;
import org.athena.framework.test.api.service.StepExecutor;
import org.athena.framework.test.api.service.TestExecutionRepository;
import org.athena.framework.test.api.service.TestPlanRepository;
import org.athena.framework.test.api.service.TestSceneDefinitionRepository;
import org.athena.framework.test.api.service.TestService;
import org.athena.framework.test.service.DefaultStepExecutorRegistry;
import org.athena.framework.test.service.DefaultTestService;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * 测试核心自动装配。
 */
@AutoConfiguration
@EnableConfigurationProperties(AthenaTestProperties.class)
public class TestAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public DefaultStepExecutorRegistry stepExecutorRegistry(List<StepExecutor> executors) {
        return new DefaultStepExecutorRegistry(executors);
    }

    @Bean
    @ConditionalOnMissingBean
    public TestService testService(DefaultStepExecutorRegistry stepExecutorRegistry,
                                   AthenaTestProperties properties,
                                   ObjectProvider<List<AssertionEvaluator>> assertionEvaluatorsProvider,
                                   ObjectProvider<TestSceneDefinitionRepository> sceneDefinitionRepositoryProvider,
                                   ObjectProvider<TestPlanRepository> planRepositoryProvider,
                                   ObjectProvider<TestExecutionRepository> executionRepositoryProvider) {
        return new DefaultTestService(
            stepExecutorRegistry,
            properties,
            assertionEvaluatorsProvider.getIfAvailable(List::of),
            sceneDefinitionRepositoryProvider.getIfAvailable(),
            planRepositoryProvider.getIfAvailable(),
            executionRepositoryProvider.getIfAvailable()
        );
    }
}
