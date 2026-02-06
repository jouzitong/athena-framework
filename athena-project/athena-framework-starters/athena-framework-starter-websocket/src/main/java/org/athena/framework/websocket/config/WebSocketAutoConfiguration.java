package org.athena.framework.websocket.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.athena.framework.websocket.backpressure.BackpressureStrategy;
import org.athena.framework.websocket.backpressure.OutboundQueueFactory;
import org.athena.framework.websocket.bus.LocalMessageBus;
import org.athena.framework.websocket.bus.MessageBus;
import org.athena.framework.websocket.gateway.ConnectionRegistry;
import org.athena.framework.websocket.gateway.DefaultWsOutbound;
import org.athena.framework.websocket.gateway.InMemoryConnectionRegistry;
import org.athena.framework.websocket.gateway.WsGatewayHandler;
import org.athena.framework.websocket.gateway.WsHandshakeInterceptor;
import org.athena.framework.websocket.gateway.WsOutbound;
import org.athena.framework.websocket.handler.DefaultRequestHandler;
import org.athena.framework.websocket.handler.PingHandler;
import org.athena.framework.websocket.handler.SubscribeHandler;
import org.athena.framework.websocket.handler.UnsubscribeHandler;
import org.athena.framework.websocket.handler.WsActionHandler;
import org.athena.framework.websocket.handler.WsHandler;
import org.athena.framework.websocket.metrics.NoopWsMetrics;
import org.athena.framework.websocket.metrics.WsMetrics;
import org.athena.framework.websocket.protocol.MessageValidator;
import org.athena.framework.websocket.protocol.WsMessageFactory;
import org.athena.framework.websocket.router.DefaultWsRouter;
import org.athena.framework.websocket.router.WsRouter;
import org.athena.framework.websocket.security.AclService;
import org.athena.framework.websocket.security.AllowAllAclService;
import org.athena.framework.websocket.security.TokenService;
import org.athena.framework.websocket.security.AllowAllTokenService;
import org.athena.framework.websocket.session.InMemorySessionManager;
import org.athena.framework.websocket.session.SessionManager;
import org.athena.framework.websocket.subscription.InMemorySubscriptionManager;
import org.athena.framework.websocket.subscription.SubscriptionManager;
import org.athena.framework.websocket.support.ResumeStore;
import org.athena.framework.websocket.support.InMemoryResumeStore;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

@AutoConfiguration
@EnableWebSocket
@ConditionalOnClass(name = "org.springframework.web.socket.WebSocketHandler")
@EnableConfigurationProperties(WebSocketProperties.class)
public class WebSocketAutoConfiguration {

    /**
     * 指标埋点接口，默认空实现，业务可覆盖
     */
    @Bean
    @ConditionalOnMissingBean
    public WsMetrics wsMetrics() {
        return new NoopWsMetrics();
    }

    /**
     * 消息总线默认本地实现，业务可替换为 Redis/Kafka
     */
    @Bean
    @ConditionalOnMissingBean
    public MessageBus messageBus() {
        return new LocalMessageBus();
    }

    @Bean
    @ConditionalOnMissingBean
    public SubscriptionManager subscriptionManager() {
        return new InMemorySubscriptionManager();
    }

    /**
     * 会话管理默认内存实现
     */
    @Bean
    @ConditionalOnMissingBean
    public SessionManager sessionManager() {
        return new InMemorySessionManager();
    }

    @Bean
    @ConditionalOnMissingBean
    public TokenService tokenService() {
        return new AllowAllTokenService();
    }

    @Bean
    @ConditionalOnMissingBean
    public AclService aclService() {
        return new AllowAllAclService();
    }

    @Bean
    @ConditionalOnMissingBean
    public ResumeStore resumeStore(WebSocketProperties properties) {
        return new InMemoryResumeStore(properties.getResumeTtlMs());
    }

    @Bean
    @ConditionalOnMissingBean
    public ConnectionRegistry connectionRegistry() {
        return new InMemoryConnectionRegistry();
    }

    @Bean
    @ConditionalOnMissingBean
    public WsOutbound wsOutbound(ObjectMapper objectMapper,
                                 ConnectionRegistry connectionRegistry,
                                 WsMetrics metrics) {
        return new DefaultWsOutbound(objectMapper, connectionRegistry, metrics);
    }

    @Bean
    @ConditionalOnMissingBean
    public MessageValidator messageValidator() {
        return new MessageValidator();
    }

    @Bean
    @ConditionalOnMissingBean
    public WsMessageFactory wsMessageFactory() {
        return new WsMessageFactory();
    }

    /**
     * 出站队列工厂，用于背压策略
     */
    @Bean
    @ConditionalOnMissingBean
    public OutboundQueueFactory outboundQueueFactory(WebSocketProperties properties) {
        BackpressureStrategy strategy = properties.getBackpressureStrategy();
        return new OutboundQueueFactory(strategy, properties.getMaxOutboundQueuePerConn());
    }

    @Bean
    @ConditionalOnMissingBean
    public PingHandler pingHandler(WsMessageFactory messageFactory, WsOutbound outbound) {
        return new PingHandler(messageFactory, outbound);
    }

    @Bean
    @ConditionalOnMissingBean
    public SubscribeHandler subscribeHandler(SubscriptionManager subscriptionManager,
                                             WsMessageFactory messageFactory,
                                             WsOutbound outbound) {
        return new SubscribeHandler(subscriptionManager, messageFactory, outbound);
    }

    @Bean
    @ConditionalOnMissingBean
    public UnsubscribeHandler unsubscribeHandler(SubscriptionManager subscriptionManager,
                                                 WsMessageFactory messageFactory,
                                                 WsOutbound outbound) {
        return new UnsubscribeHandler(subscriptionManager, messageFactory, outbound);
    }

    @Bean
    @ConditionalOnMissingBean
    public DefaultRequestHandler defaultRequestHandler(List<WsActionHandler> actionHandlers,
                                                       SubscriptionManager subscriptionManager,
                                                       ResumeStore resumeStore,
                                                       WsMessageFactory messageFactory,
                                                       WebSocketProperties properties,
                                                       WsOutbound outbound) {
        return new DefaultRequestHandler(actionHandlers, subscriptionManager, resumeStore, messageFactory,
            properties.getResumeTtlMs(), outbound);
    }

    @Bean
    @ConditionalOnMissingBean
    public WsRouter wsRouter(List<WsHandler> handlers,
                             AclService aclService,
                             WsMetrics metrics) {
        return new DefaultWsRouter(handlers, aclService, metrics);
    }

    @Bean
    @ConditionalOnMissingBean
    public WsHandshakeInterceptor wsHandshakeInterceptor(TokenService tokenService, WsMetrics metrics) {
        return new WsHandshakeInterceptor(tokenService, metrics);
    }

    @Bean
    @ConditionalOnMissingBean
    public WsGatewayHandler wsGatewayHandler(ObjectMapper objectMapper,
                                             SessionManager sessionManager,
                                             WsRouter router,
                                             MessageValidator validator,
                                             WsMessageFactory messageFactory,
                                             ConnectionRegistry connectionRegistry,
                                             SubscriptionManager subscriptionManager,
                                             ResumeStore resumeStore,
                                             WsOutbound outbound,
                                             WsMetrics metrics) {
        return new WsGatewayHandler(objectMapper, sessionManager, router, validator,
            messageFactory, connectionRegistry, subscriptionManager, resumeStore, outbound,
            metrics);
    }

    @Bean
    @ConditionalOnMissingBean
    public WsWebSocketConfigurer wsWebSocketConfigurer(WebSocketProperties properties,
                                                       WsGatewayHandler gatewayHandler,
                                                       WsHandshakeInterceptor handshakeInterceptor) {
        return new WsWebSocketConfigurer(properties, gatewayHandler, handshakeInterceptor);
    }
}
