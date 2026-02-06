package org.athena.framework.websocket.subscription;

import java.util.Set;

public interface SubscriptionManager {

    void subscribe(String connId, String topic);

    void unsubscribe(String connId, String topic);

    void removeConnection(String connId);

    Set<String> getTopicsByConn(String connId);

    Set<String> getConnsByTopic(String topic);
}
