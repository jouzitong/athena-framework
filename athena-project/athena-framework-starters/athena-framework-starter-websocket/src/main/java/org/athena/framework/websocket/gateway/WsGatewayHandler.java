package org.athena.framework.websocket.gateway;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.athena.framework.websocket.metrics.WsMetrics;
import org.athena.framework.websocket.protocol.MessageValidator;
import org.athena.framework.websocket.protocol.WsMessage;
import org.athena.framework.websocket.protocol.WsMessageFactory;
import org.athena.framework.websocket.protocol.WsMessageType;
import org.athena.framework.websocket.protocol.WsMeta;
import org.athena.framework.websocket.router.WsRouter;
import org.athena.framework.websocket.session.SessionManager;
import org.athena.framework.websocket.session.WsSession;
import org.athena.framework.websocket.subscription.SubscriptionManager;
import org.athena.framework.websocket.support.ResumeStore;
import org.athena.framework.websocket.support.SessionSnapshot;
import org.athena.framework.websocket.support.WsProtocolException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WsGatewayHandler extends TextWebSocketHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(WsGatewayHandler.class);

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
        // 建立连接：生成 connId，创建业务会话，并保存到管理器
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
        // 连接成功后返回 HELLO（携带 resumeId / clientId）
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
            // 1) JSON 反序列化
            parsed = objectMapper.readValue(message.getPayload(), WsMessage.class);
            // 2) 读取 meta 中的 clientId / resumeId（可由客户端主动更新）
            applyMeta(wsSession, parsed.getMeta());
            // 3) 协议校验
            validator.validate(parsed);
            metrics.onInboundMessage(parsed.getType());
            // 4) 路由分发
            router.route(wsSession, parsed);
        } catch (WsProtocolException ex) {
            // 协议类错误统一回 ERROR
            WsMessage error = messageFactory.errorResponse(parsed,
                ex.getCode() == null ? "INTERNAL_ERROR" : ex.getCode().name(),
                ex.getMessage(), false);
            outbound.send(wsSession, error);
        } catch (IOException ex) {
            // JSON 解析失败
            WsMessage error = messageFactory.errorResponse(new WsMessage(),
                "BAD_SCHEMA", "invalid json", false);
            outbound.send(wsSession, error);
        } catch (Throwable ex) {
            // 兜底：业务运行时异常统一回 INTERNAL_ERROR，避免客户端无回包
            LOGGER.error("ws internal error, connId={}, userId={}",
                wsSession.getConnId(), wsSession.getUserId(), ex);
            WsMessage base = parsed == null ? new WsMessage() : parsed;
            WsMessage error = messageFactory.errorResponse(base, "INTERNAL_ERROR", "internal error", false);
            outbound.send(wsSession, error);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        WsSession wsSession = findSession(session);
        if (wsSession != null) {
            // 断线时保存快照，用于 5 分钟内恢复订阅
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
            // 发生底层 IO 错误时主动关闭连接
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
        // 客户端可在运行中更新 clientId / resumeId
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
        hello.setType(WsMessageType.HELLO);
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
        // 保存当前订阅集合与用户信息，用于异常断线恢复
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
