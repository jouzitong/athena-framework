package org.athena.framework.websocket.gateway;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.athena.framework.websocket.metrics.WsMetrics;
import org.athena.framework.websocket.protocol.WsMessage;
import org.athena.framework.websocket.session.WsSession;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

/**
 * 默认出站发送器
 */
public class DefaultWsOutbound implements WsOutbound {

    private final ObjectMapper objectMapper;
    private final ConnectionRegistry connectionRegistry;
    private final WsMetrics metrics;

    public DefaultWsOutbound(ObjectMapper objectMapper,
                             ConnectionRegistry connectionRegistry,
                             WsMetrics metrics) {
        this.objectMapper = objectMapper;
        this.connectionRegistry = connectionRegistry;
        this.metrics = metrics;
    }

    @Override
    public void send(WsSession session, WsMessage message) {
        if (session == null || message == null) {
            return;
        }
        WebSocketSession wsSession = connectionRegistry.get(session.getConnId());
        if (wsSession == null) {
            return;
        }
        try {
            String payload = objectMapper.writeValueAsString(message);
            wsSession.sendMessage(new TextMessage(payload));
            metrics.onOutboundMessage(message.getType());
        } catch (JsonProcessingException e) {
            // 序列化失败，当前实现不抛出，避免影响主流程
        } catch (IOException e) {
            // 发送失败，当前实现不抛出，避免影响主流程
        }
    }
}
