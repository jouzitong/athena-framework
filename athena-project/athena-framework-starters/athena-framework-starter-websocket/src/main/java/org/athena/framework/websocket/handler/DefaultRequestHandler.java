package org.athena.framework.websocket.handler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.athena.framework.websocket.gateway.WsOutbound;
import org.athena.framework.websocket.protocol.WsMessage;
import org.athena.framework.websocket.protocol.WsMessageFactory;
import org.athena.framework.websocket.session.WsSession;
import org.athena.framework.websocket.subscription.SubscriptionManager;
import org.athena.framework.websocket.support.ResumeStore;
import org.athena.framework.websocket.support.SessionSnapshot;
import org.athena.framework.websocket.support.WsErrorCode;

/**
 * 默认 REQUEST 处理器，内置 RESUME 动作
 */
public class DefaultRequestHandler implements WsHandler {

    private final List<WsActionHandler> actionHandlers;
    private final SubscriptionManager subscriptionManager;
    private final ResumeStore resumeStore;
    private final WsMessageFactory messageFactory;
    private final long resumeTtlMs;
    private final WsOutbound outbound;

    public DefaultRequestHandler(List<WsActionHandler> actionHandlers,
                                 SubscriptionManager subscriptionManager,
                                 ResumeStore resumeStore,
                                 WsMessageFactory messageFactory,
                                 long resumeTtlMs,
                                 WsOutbound outbound) {
        this.actionHandlers = actionHandlers;
        this.subscriptionManager = subscriptionManager;
        this.resumeStore = resumeStore;
        this.messageFactory = messageFactory;
        this.resumeTtlMs = resumeTtlMs;
        this.outbound = outbound;
    }

    @Override
    public boolean supports(String type) {
        return "REQUEST".equals(type);
    }

    @Override
    public void handle(WsSession session, WsMessage message) {
        String action = extractAction(message.getPayload());
        // 内置动作：RESUME
        if ("RESUME".equals(action)) {
            handleResume(session, message);
            return;
        }
        // 自定义动作通过 WsActionHandler 扩展
        for (WsActionHandler handler : actionHandlers) {
            if (handler.supports(action)) {
                handler.handle(session, message);
                return;
            }
        }
        // 未匹配到动作，返回错误响应
        Map<String, Object> payload = new HashMap<>();
        payload.put("status", "ERROR");
        payload.put("code", "UNKNOWN_ACTION");
        WsMessage response = messageFactory.okResponse(message, payload);
        outbound.send(session, response);
    }

    private void handleResume(WsSession session, WsMessage message) {
        String resumeId = extractResumeId(message.getPayload());
        if (resumeId == null || resumeId.trim().isEmpty()) {
            WsMessage error = messageFactory.errorResponse(message, WsErrorCode.RESUME_NOT_FOUND.name(),
                "resumeId is required", false);
            outbound.send(session, error);
            return;
        }
        SessionSnapshot snapshot = resumeStore.get(resumeId);
        if (snapshot == null) {
            WsMessage error = messageFactory.errorResponse(message, WsErrorCode.RESUME_EXPIRED.name(),
                "resumeId expired or not found", false);
            outbound.send(session, error);
            return;
        }
        // 强绑定 userId，避免 resumeId 被他人使用
        if (!snapshot.getUserId().equals(session.getUserId())) {
            WsMessage error = messageFactory.errorResponse(message, WsErrorCode.RESUME_FORBIDDEN.name(),
                "resumeId does not match user", false);
            outbound.send(session, error);
            return;
        }
        long now = System.currentTimeMillis();
        if (now - snapshot.getLastSeenAt() > resumeTtlMs) {
            WsMessage error = messageFactory.errorResponse(message, WsErrorCode.RESUME_EXPIRED.name(),
                "resumeId expired", false);
            outbound.send(session, error);
            return;
        }
        // 恢复订阅集合
        Set<String> topics = snapshot.getTopics();
        for (String topic : topics) {
            subscriptionManager.subscribe(session.getConnId(), topic);
        }
        Map<String, Object> payload = new HashMap<>();
        payload.put("status", "OK");
        payload.put("restoredTopics", topics);
        WsMessage response = messageFactory.okResponse(message, payload);
        outbound.send(session, response);
    }

    private String extractAction(Object payload) {
        if (payload instanceof Map) {
            Object action = ((Map<?, ?>) payload).get("action");
            if (action != null) {
                return String.valueOf(action);
            }
        }
        return "";
    }

    private String extractResumeId(Object payload) {
        if (payload instanceof Map) {
            Object resumeId = ((Map<?, ?>) payload).get("resumeId");
            if (resumeId != null) {
                return String.valueOf(resumeId);
            }
        }
        return null;
    }
}
