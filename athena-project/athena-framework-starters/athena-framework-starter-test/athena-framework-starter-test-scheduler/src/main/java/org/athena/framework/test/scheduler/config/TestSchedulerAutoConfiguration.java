package org.athena.framework.test.scheduler.config;

import org.athena.framework.test.api.service.TestService;
import org.athena.framework.test.scheduler.service.DefaultTestPlanScheduler;
import org.athena.framework.test.scheduler.service.TestPlanScheduler;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * 测试计划调度自动装配。
 */
@AutoConfiguration
public class TestSchedulerAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public TestPlanScheduler testPlanScheduler(TestService testService) {
        return new DefaultTestPlanScheduler(testService);
    }
}
