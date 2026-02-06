package org.athena.framework.websocket.subscription;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class InMemorySubscriptionManager implements SubscriptionManager {

    private final ConcurrentHashMap<String, Set<String>> topicToConns = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Set<String>> connToTopics = new ConcurrentHashMap<>();

    @Override
    public void subscribe(String connId, String topic) {
        if (connId == null || topic == null) {
            return;
        }
        // 双向索引，便于按 topic 或连接快速清理
        topicToConns.computeIfAbsent(topic, key -> ConcurrentHashMap.newKeySet()).add(connId);
        connToTopics.computeIfAbsent(connId, key -> ConcurrentHashMap.newKeySet()).add(topic);
    }

    @Override
    public void unsubscribe(String connId, String topic) {
        if (connId == null || topic == null) {
            return;
        }
        Set<String> conns = topicToConns.get(topic);
        if (conns != null) {
            conns.remove(connId);
        }
        Set<String> topics = connToTopics.get(connId);
        if (topics != null) {
            topics.remove(topic);
        }
    }

    @Override
    public void removeConnection(String connId) {
        if (connId == null) {
            return;
        }
        Set<String> topics = connToTopics.remove(connId);
        if (topics == null) {
            return;
        }
        // 连接断开时，清理所有 topic 的反向引用
        for (String topic : topics) {
            Set<String> conns = topicToConns.get(topic);
            if (conns != null) {
                conns.remove(connId);
            }
        }
    }

    @Override
    public Set<String> getTopicsByConn(String connId) {
        Set<String> topics = connToTopics.get(connId);
        if (topics == null) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableSet(topics);
    }

    @Override
    public Set<String> getConnsByTopic(String topic) {
        Set<String> conns = topicToConns.get(topic);
        if (conns == null) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableSet(conns);
    }
}
