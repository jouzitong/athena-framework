package org.athena.framework.websocket.handler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.athena.framework.websocket.gateway.WsOutbound;
import org.athena.framework.websocket.protocol.WsMessage;
import org.athena.framework.websocket.protocol.WsMessageFactory;
import org.athena.framework.websocket.protocol.WsActionType;
import org.athena.framework.websocket.protocol.WsMessageType;
import org.athena.framework.websocket.protocol.WsPayloadKey;
import org.athena.framework.websocket.protocol.WsPayloadStatus;
import org.athena.framework.websocket.session.WsSession;
import org.athena.framework.websocket.subscription.SubscriptionManager;
import org.athena.framework.websocket.support.ResumeStore;
import org.athena.framework.websocket.support.SessionSnapshot;
import org.athena.framework.websocket.support.WsErrorCode;

/**
 * 默认 REQUEST 处理器，内置 RESUME 动作
 */
public class DefaultRequestHandler extends AbstractWsHandler {

    private final List<WsActionHandler> actionHandlers;
    private final SubscriptionManager subscriptionManager;
    private final ResumeStore resumeStore;
    private final long resumeTtlMs;
    public DefaultRequestHandler(List<WsActionHandler> actionHandlers,
                                 SubscriptionManager subscriptionManager,
                                 ResumeStore resumeStore,
                                 WsMessageFactory messageFactory,
                                 long resumeTtlMs,
                                 WsOutbound outbound) {
        super(WsMessageType.REQUEST, messageFactory, outbound);
        this.actionHandlers = actionHandlers;
        this.subscriptionManager = subscriptionManager;
        this.resumeStore = resumeStore;
        this.resumeTtlMs = resumeTtlMs;
    }

    @Override
    public void handle(WsSession session, WsMessage message) {
        String action = extractAction(message.getPayload());
        // 内置动作：RESUME
        if (WsActionType.RESUME.equals(action)) {
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
        payload.put(WsPayloadKey.STATUS, WsPayloadStatus.ERROR);
        payload.put(WsPayloadKey.CODE, WsErrorCode.UNKNOWN_ACTION.name());
        sendOk(session, message, payload);
    }

    private void handleResume(WsSession session, WsMessage message) {
        String resumeId = extractResumeId(message.getPayload());
        if (resumeId == null || resumeId.trim().isEmpty()) {
            sendError(session, message, WsErrorCode.RESUME_NOT_FOUND.name(), "resumeId is required");
            return;
        }
        SessionSnapshot snapshot = resumeStore.get(resumeId);
        if (snapshot == null) {
            sendError(session, message, WsErrorCode.RESUME_EXPIRED.name(), "resumeId expired or not found");
            return;
        }
        // 强绑定 userId，避免 resumeId 被他人使用
        if (!snapshot.getUserId().equals(session.getUserId())) {
            sendError(session, message, WsErrorCode.RESUME_FORBIDDEN.name(), "resumeId does not match user");
            return;
        }
        long now = System.currentTimeMillis();
        if (now - snapshot.getLastSeenAt() > resumeTtlMs) {
            sendError(session, message, WsErrorCode.RESUME_EXPIRED.name(), "resumeId expired");
            return;
        }
        // 恢复订阅集合
        Set<String> topics = snapshot.getTopics();
        for (String topic : topics) {
            subscriptionManager.subscribe(session.getConnId(), topic);
        }
        Map<String, Object> payload = new HashMap<>();
        payload.put(WsPayloadKey.STATUS, WsPayloadStatus.OK);
        payload.put(WsPayloadKey.RESTORED_TOPICS, topics);
        sendOk(session, message, payload);
    }

    private String extractAction(Object payload) {
        if (payload instanceof Map) {
            Object action = ((Map<?, ?>) payload).get(WsPayloadKey.ACTION);
            if (action != null) {
                return String.valueOf(action);
            }
        }
        return "";
    }

    private String extractResumeId(Object payload) {
        if (payload instanceof Map) {
            Object resumeId = ((Map<?, ?>) payload).get(WsPayloadKey.RESUME_ID);
            if (resumeId != null) {
                return String.valueOf(resumeId);
            }
        }
        return null;
    }
}
