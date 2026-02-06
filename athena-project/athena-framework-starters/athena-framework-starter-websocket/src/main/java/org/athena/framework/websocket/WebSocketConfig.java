package org.athena.framework.websocket;

import org.athena.framework.websocket.config.WebSocketAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 *
 * @author zhouzhitong
 * @since 2026/2/6
 */
@Configuration
@Import(WebSocketAutoConfiguration.class)
public class WebSocketConfig {
}
