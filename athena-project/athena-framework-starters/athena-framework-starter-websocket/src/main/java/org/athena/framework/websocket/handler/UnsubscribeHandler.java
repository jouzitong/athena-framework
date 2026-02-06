package org.athena.framework.websocket.handler;

import java.util.HashMap;
import java.util.Map;
import org.athena.framework.websocket.gateway.WsOutbound;
import org.athena.framework.websocket.protocol.WsMessage;
import org.athena.framework.websocket.protocol.WsMessageFactory;
import org.athena.framework.websocket.protocol.WsMessageType;
import org.athena.framework.websocket.protocol.WsPayloadKey;
import org.athena.framework.websocket.session.WsSession;
import org.athena.framework.websocket.subscription.SubscriptionManager;

/**
 * 取消订阅处理器
 */
public class UnsubscribeHandler extends AbstractWsHandler {

    private final SubscriptionManager subscriptionManager;

    public UnsubscribeHandler(SubscriptionManager subscriptionManager,
                              WsMessageFactory messageFactory,
                              WsOutbound outbound) {
        super(WsMessageType.UNSUBSCRIBE, messageFactory, outbound);
        this.subscriptionManager = subscriptionManager;
    }

    @Override
    public void handle(WsSession session, WsMessage message) {
        // 解除订阅关系
        subscriptionManager.unsubscribe(session.getConnId(), message.getTopic());
        // 取消订阅回包
        Map<String, Object> payload = new HashMap<>();
        payload.put(WsPayloadKey.SUBSCRIBED, false);
        sendOk(session, message, payload);
    }
}
