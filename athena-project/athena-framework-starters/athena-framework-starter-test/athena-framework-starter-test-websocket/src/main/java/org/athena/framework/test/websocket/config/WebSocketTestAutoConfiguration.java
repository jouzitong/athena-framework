package org.athena.framework.test.websocket.config;

import org.athena.framework.test.api.service.StepExecutor;
import org.athena.framework.test.websocket.executor.WebSocketStepExecutor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * WebSocket 测试执行器自动装配。
 */
@AutoConfiguration
public class WebSocketTestAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(name = "webSocketStepExecutor")
    public StepExecutor webSocketStepExecutor() {
        return new WebSocketStepExecutor();
    }
}
