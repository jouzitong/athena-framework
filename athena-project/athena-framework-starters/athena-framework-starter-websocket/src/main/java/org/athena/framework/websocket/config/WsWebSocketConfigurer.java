package org.athena.framework.websocket.config;

import org.athena.framework.websocket.gateway.WsGatewayHandler;
import org.athena.framework.websocket.gateway.WsHandshakeInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

public class WsWebSocketConfigurer implements WebSocketConfigurer {

    private final WebSocketProperties properties;
    private final WsGatewayHandler gatewayHandler;
    private final WsHandshakeInterceptor handshakeInterceptor;

    @Autowired
    public WsWebSocketConfigurer(WebSocketProperties properties,
                                 WsGatewayHandler gatewayHandler,
                                 WsHandshakeInterceptor handshakeInterceptor) {
        this.properties = properties;
        this.gatewayHandler = gatewayHandler;
        this.handshakeInterceptor = handshakeInterceptor;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(gatewayHandler, properties.getEndpoint())
            .addInterceptors(handshakeInterceptor)
            .setAllowedOrigins("*");
    }
}
