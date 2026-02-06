package org.athena.framework.websocket.subscription;

import java.util.Set;

/**
 * 订阅管理接口
 */
public interface SubscriptionManager {

    /**
     * 订阅 topic
     */
    void subscribe(String connId, String topic);

    /**
     * 取消订阅
     */
    void unsubscribe(String connId, String topic);

    /**
     * 连接断开时清理订阅
     */
    void removeConnection(String connId);

    /**
     * 获取连接订阅的 topics
     */
    Set<String> getTopicsByConn(String connId);

    /**
     * 获取订阅该 topic 的连接
     */
    Set<String> getConnsByTopic(String topic);
}
