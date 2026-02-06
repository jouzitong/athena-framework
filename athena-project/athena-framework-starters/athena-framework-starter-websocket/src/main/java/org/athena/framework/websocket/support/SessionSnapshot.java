package org.athena.framework.websocket.support;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class SessionSnapshot {

    private final String resumeId;
    private final String userId;
    private final Set<String> topics;
    private final long lastSeenAt;
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
