package org.athena.framework.websocket.bus;

import java.util.function.Consumer;
import org.athena.framework.websocket.protocol.WsMessage;

/**
 * 消息总线抽象
 */
public interface MessageBus {

    /**
     * 发布消息到指定 topic
     */
    void publish(String topic, WsMessage message);

    /**
     * 订阅指定 topic
     */
    void subscribe(String topic, Consumer<WsMessage> consumer);
}
