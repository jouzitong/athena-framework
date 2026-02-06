package org.athena.framework.websocket.gateway;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.athena.framework.websocket.metrics.WsMetrics;
import org.athena.framework.websocket.protocol.WsMessage;
import org.athena.framework.websocket.session.WsSession;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

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
            // swallow serialization errors for now
        } catch (IOException e) {
            // swallow transport errors for now
        }
    }
}
