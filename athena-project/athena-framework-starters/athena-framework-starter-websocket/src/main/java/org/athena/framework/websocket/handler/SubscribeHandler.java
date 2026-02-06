package org.athena.framework.websocket.handler;

import java.util.HashMap;
import java.util.Map;
import org.athena.framework.websocket.bus.TopicDispatcher;
import org.athena.framework.websocket.gateway.WsOutbound;
import org.athena.framework.websocket.protocol.WsMessage;
import org.athena.framework.websocket.protocol.WsMessageFactory;
import org.athena.framework.websocket.protocol.WsMessageType;
import org.athena.framework.websocket.protocol.WsPayloadKey;
import org.athena.framework.websocket.session.WsSession;
import org.athena.framework.websocket.subscription.SubscriptionManager;

/**
 * 订阅处理器
 */
public class SubscribeHandler extends AbstractWsHandler {

    private final SubscriptionManager subscriptionManager;
    private final TopicDispatcher topicDispatcher;

    public SubscribeHandler(SubscriptionManager subscriptionManager,
                            WsMessageFactory messageFactory,
                            WsOutbound outbound,
                            TopicDispatcher topicDispatcher) {
        super(WsMessageType.SUBSCRIBE, messageFactory, outbound);
        this.subscriptionManager = subscriptionManager;
        this.topicDispatcher = topicDispatcher;
    }

    @Override
    public void handle(WsSession session, WsMessage message) {
        // 保存订阅关系
        subscriptionManager.subscribe(session.getConnId(), message.getTopic());
        // 注册 topic 分发（本地模式下订阅消息总线）
        topicDispatcher.register(message.getTopic());
        // 订阅成功回包
        Map<String, Object> payload = new HashMap<>();
        payload.put(WsPayloadKey.SUBSCRIBED, true);
        sendOk(session, message, payload);
    }
}
