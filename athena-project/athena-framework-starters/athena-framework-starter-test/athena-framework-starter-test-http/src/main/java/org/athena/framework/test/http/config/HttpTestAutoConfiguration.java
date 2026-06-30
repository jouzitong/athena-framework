package org.athena.framework.test.http.config;

import org.athena.framework.test.api.service.StepExecutor;
import org.athena.framework.test.http.executor.HttpStepExecutor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * HTTP 测试执行器自动装配。
 */
@AutoConfiguration
public class HttpTestAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(name = "httpStepExecutor")
    public StepExecutor httpStepExecutor() {
        return new HttpStepExecutor();
    }
}
