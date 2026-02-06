package org.athena.framework.websocket.gateway;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.athena.framework.websocket.metrics.WsMetrics;
import org.athena.framework.websocket.protocol.MessageValidator;
import org.athena.framework.websocket.protocol.WsMessage;
import org.athena.framework.websocket.protocol.WsMessageFactory;
import org.athena.framework.websocket.protocol.WsMeta;
import org.athena.framework.websocket.router.WsRouter;
import org.athena.framework.websocket.session.SessionManager;
import org.athena.framework.websocket.session.WsSession;
import org.athena.framework.websocket.subscription.SubscriptionManager;
import org.athena.framework.websocket.support.ResumeStore;
import org.athena.framework.websocket.support.SessionSnapshot;
import org.athena.framework.websocket.support.WsProtocolException;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class WsGatewayHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper;
    private final SessionManager sessionManager;
    private final WsRouter router;
    private final MessageValidator validator;
    private final WsMessageFactory messageFactory;
    private final ConnectionRegistry connectionRegistry;
    private final SubscriptionManager subscriptionManager;
    private final ResumeStore resumeStore;
    private final WsOutbound outbound;
    private final WsMetrics metrics;

    public WsGatewayHandler(ObjectMapper objectMapper,
                            SessionManager sessionManager,
                            WsRouter router,
                            MessageValidator validator,
                            WsMessageFactory messageFactory,
                            ConnectionRegistry connectionRegistry,
                            SubscriptionManager subscriptionManager,
                            ResumeStore resumeStore,
                            WsOutbound outbound,
                            WsMetrics metrics) {
        this.objectMapper = objectMapper;
        this.sessionManager = sessionManager;
        this.router = router;
        this.validator = validator;
        this.messageFactory = messageFactory;
        this.connectionRegistry = connectionRegistry;
        this.subscriptionManager = subscriptionManager;
        this.resumeStore = resumeStore;
        this.outbound = outbound;
        this.metrics = metrics;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String connId = UUID.randomUUID().toString();
        String userId = (String) session.getAttributes().get(WsHandshakeInterceptor.ATTR_USER_ID);
        @SuppressWarnings("unchecked")
        Map<String, Object> claims = (Map<String, Object>) session.getAttributes()
            .get(WsHandshakeInterceptor.ATTR_CLAIMS);
        String clientId = (String) session.getAttributes().get(WsHandshakeInterceptor.ATTR_CLIENT_ID);
        String resumeId = (String) session.getAttributes().get(WsHandshakeInterceptor.ATTR_RESUME_ID);
        if (resumeId == null || resumeId.trim().isEmpty()) {
            resumeId = "r-" + UUID.randomUUID();
        }
        WsSession wsSession = new WsSession(connId, userId, claims, clientId, resumeId, System.currentTimeMillis());
        wsSession.setOutboundQueueSize(0);
        sessionManager.addSession(wsSession);
        connectionRegistry.add(connId, session);
        session.getAttributes().put("ws.connId", connId);
        metrics.onConnectionOpen();
        sendHello(wsSession);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        WsSession wsSession = findSession(session);
        if (wsSession == null) {
            return;
        }
        wsSession.touch(System.currentTimeMillis());
        WsMessage parsed = null;
        try {
            parsed = objectMapper.readValue(message.getPayload(), WsMessage.class);
            applyMeta(wsSession, parsed.getMeta());
            validator.validate(parsed);
            metrics.onInboundMessage(parsed.getType());
            router.route(wsSession, parsed);
        } catch (WsProtocolException ex) {
            WsMessage error = messageFactory.errorResponse(parsed,
                ex.getCode() == null ? "INTERNAL_ERROR" : ex.getCode().name(),
                ex.getMessage(), false);
            outbound.send(wsSession, error);
        } catch (IOException ex) {
            WsMessage error = messageFactory.errorResponse(new WsMessage(),
                "BAD_SCHEMA", "invalid json", false);
            outbound.send(wsSession, error);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        WsSession wsSession = findSession(session);
        if (wsSession != null) {
            saveSnapshot(wsSession);
            sessionManager.removeSession(wsSession.getConnId());
            subscriptionManager.removeConnection(wsSession.getConnId());
            connectionRegistry.remove(wsSession.getConnId());
        }
        metrics.onConnectionClosed(status.getCode());
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        try {
            session.close(CloseStatus.SERVER_ERROR);
        } catch (IOException ignored) {
        }
    }

    private WsSession findSession(WebSocketSession session) {
        Object connId = session.getAttributes().get("ws.connId");
        if (connId == null) {
            return null;
        }
        return sessionManager.getSession(String.valueOf(connId));
    }

    private void applyMeta(WsSession session, WsMeta meta) {
        if (meta == null) {
            return;
        }
        if (meta.getClientId() != null && !meta.getClientId().trim().isEmpty()) {
            session.setClientId(meta.getClientId());
        }
        if (meta.getResumeId() != null && !meta.getResumeId().trim().isEmpty()) {
            session.setResumeId(meta.getResumeId());
        }
    }

    private void sendHello(WsSession session) {
        WsMessage hello = new WsMessage();
        hello.setVersion("1.0");
        hello.setType("HELLO");
        hello.setTimestamp(System.currentTimeMillis());
        Map<String, Object> payload = new HashMap<>();
        payload.put("serverTime", System.currentTimeMillis());
        hello.setPayload(payload);
        WsMeta meta = new WsMeta();
        meta.setClientId(session.getClientId());
        meta.setResumeId(session.getResumeId());
        hello.setMeta(meta);
        outbound.send(session, hello);
    }

    private void saveSnapshot(WsSession session) {
        SessionSnapshot snapshot = new SessionSnapshot(
            session.getResumeId(),
            session.getUserId(),
            subscriptionManager.getTopicsByConn(session.getConnId()),
            System.currentTimeMillis(),
            session.getClientId()
        );
        resumeStore.save(snapshot);
    }
}
