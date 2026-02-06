package org.athena.framework.websocket.bus;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import org.athena.framework.websocket.protocol.WsMessage;

/**
 * 本地内存消息总线
 */
public class LocalMessageBus implements MessageBus {

    private final Map<String, List<Consumer<WsMessage>>> subscribers = new ConcurrentHashMap<>();

    @Override
    public void publish(String topic, WsMessage message) {
        List<Consumer<WsMessage>> consumers = subscribers.get(topic);
        if (consumers == null) {
            return;
        }
        // 本地发布：直接回调本地消费者
        for (Consumer<WsMessage> consumer : consumers) {
            consumer.accept(message);
        }
    }

    @Override
    public void subscribe(String topic, Consumer<WsMessage> consumer) {
        if (topic == null || consumer == null) {
            return;
        }
        subscribers.computeIfAbsent(topic, key -> new CopyOnWriteArrayList<>()).add(consumer);
    }
}
