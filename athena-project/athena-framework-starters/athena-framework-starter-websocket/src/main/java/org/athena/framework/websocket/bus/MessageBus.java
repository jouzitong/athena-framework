package org.athena.framework.websocket.bus;

import java.util.function.Consumer;
import org.athena.framework.websocket.protocol.WsMessage;

public interface MessageBus {

    void publish(String topic, WsMessage message);

    void subscribe(String topic, Consumer<WsMessage> consumer);
}
