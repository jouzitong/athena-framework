package org.athena.framework.websocket.handler;

import java.util.HashMap;
import java.util.Map;
import org.athena.framework.websocket.gateway.WsOutbound;
import org.athena.framework.websocket.protocol.WsMessage;
import org.athena.framework.websocket.protocol.WsMessageFactory;
import org.athena.framework.websocket.session.WsSession;
import org.athena.framework.websocket.subscription.SubscriptionManager;

public class SubscribeHandler implements WsHandler {

    private final SubscriptionManager subscriptionManager;
    private final WsMessageFactory messageFactory;
    private final WsOutbound outbound;

    public SubscribeHandler(SubscriptionManager subscriptionManager,
                            WsMessageFactory messageFactory,
                            WsOutbound outbound) {
        this.subscriptionManager = subscriptionManager;
        this.messageFactory = messageFactory;
        this.outbound = outbound;
    }

    @Override
    public boolean supports(String type) {
        return "SUBSCRIBE".equals(type);
    }

    @Override
    public void handle(WsSession session, WsMessage message) {
        subscriptionManager.subscribe(session.getConnId(), message.getTopic());
        Map<String, Object> payload = new HashMap<>();
        payload.put("status", "OK");
        payload.put("subscribed", true);
        WsMessage response = messageFactory.okResponse(message, payload);
        outbound.send(session, response);
    }
}
