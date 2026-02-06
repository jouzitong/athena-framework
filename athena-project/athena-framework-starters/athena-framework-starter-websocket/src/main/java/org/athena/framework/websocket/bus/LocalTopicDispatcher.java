package org.athena.framework.websocket.bus;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.athena.framework.websocket.gateway.WsOutbound;
import org.athena.framework.websocket.protocol.WsMessage;
import org.athena.framework.websocket.session.SessionManager;
import org.athena.framework.websocket.session.WsSession;
import org.athena.framework.websocket.subscription.SubscriptionManager;

/**
 * 本地分发器：订阅本地 MessageBus，并向本机连接推送
 */
public class LocalTopicDispatcher implements TopicDispatcher {

    private final MessageBus messageBus;
    private final SubscriptionManager subscriptionManager;
    private final SessionManager sessionManager;
    private final WsOutbound outbound;
    private final Set<String> registered = ConcurrentHashMap.newKeySet();

    public LocalTopicDispatcher(MessageBus messageBus,
                                SubscriptionManager subscriptionManager,
                                SessionManager sessionManager,
                                WsOutbound outbound) {
        this.messageBus = messageBus;
        this.subscriptionManager = subscriptionManager;
        this.sessionManager = sessionManager;
        this.outbound = outbound;
    }

    @Override
    public void register(String topic) {
        if (topic == null || topic.trim().isEmpty()) {
            return;
        }
        // 避免重复订阅
        if (!registered.add(topic)) {
            return;
        }
        messageBus.subscribe(topic, this::dispatch);
    }

    @Override
    public void dispatch(WsMessage message) {
        if (message == null || message.getTopic() == null) {
            return;
        }
        Set<String> conns = subscriptionManager.getConnsByTopic(message.getTopic());
        if (conns.isEmpty()) {
            return;
        }
        for (String connId : conns) {
            WsSession session = sessionManager.getSession(connId);
            if (session != null) {
                outbound.send(session, message);
            }
        }
    }
}
