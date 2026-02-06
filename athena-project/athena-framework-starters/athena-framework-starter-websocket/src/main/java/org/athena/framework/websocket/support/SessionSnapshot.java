package org.athena.framework.websocket.support;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * 会话快照（用于断线恢复）
 */
public class SessionSnapshot {

    /**
     * 会话恢复 ID
     */
    private final String resumeId;
    /**
     * 用户 ID
     */
    private final String userId;
    /**
     * 订阅集合
     */
    private final Set<String> topics;
    /**
     * 最后活跃时间
     */
    private final long lastSeenAt;
    /**
     * 客户端标识
     */
    private final String clientId;

    public SessionSnapshot(String resumeId, String userId, Set<String> topics, long lastSeenAt, String clientId) {
        this.resumeId = resumeId;
        this.userId = userId;
        this.topics = topics == null ? Collections.emptySet() : new HashSet<>(topics);
        this.lastSeenAt = lastSeenAt;
        this.clientId = clientId;
    }

    public String getResumeId() {
        return resumeId;
    }

    public String getUserId() {
        return userId;
    }

    public Set<String> getTopics() {
        return Collections.unmodifiableSet(topics);
    }

    public long getLastSeenAt() {
        return lastSeenAt;
    }

    public String getClientId() {
        return clientId;
    }
}
