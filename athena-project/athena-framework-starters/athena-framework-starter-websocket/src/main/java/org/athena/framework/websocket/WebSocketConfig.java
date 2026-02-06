package org.athena.framework.websocket;

import org.athena.framework.websocket.config.WebSocketAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Starter 入口配置，导入自动装配
 * @author zhouzhitong
 * @since 2026/2/6
 */
@Configuration
@Import(WebSocketAutoConfiguration.class)
public class WebSocketConfig {
}
