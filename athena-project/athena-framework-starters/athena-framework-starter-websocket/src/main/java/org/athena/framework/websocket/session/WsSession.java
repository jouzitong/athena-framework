package org.athena.framework.websocket.session;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class WsSession {

    private final String connId;
    private final String userId;
    private final Map<String, Object> claims;
    private String clientId;
    private String resumeId;
    private final long createdAt;
    private final AtomicLong lastSeenAt = new AtomicLong();
    private volatile int outboundQueueSize;

    public WsSession(String connId, String userId, Map<String, Object> claims,
                     String clientId, String resumeId, long createdAt) {
        this.connId = connId;
        this.userId = userId;
        this.claims = claims == null ? Collections.emptyMap() : claims;
        this.clientId = clientId;
        this.resumeId = resumeId;
        this.createdAt = createdAt;
        this.lastSeenAt.set(createdAt);
    }

    public String getConnId() {
        return connId;
    }

    public String getUserId() {
        return userId;
    }

    public Map<String, Object> getClaims() {
        return claims;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getResumeId() {
        return resumeId;
    }

    public void setResumeId(String resumeId) {
        this.resumeId = resumeId;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getLastSeenAt() {
        return lastSeenAt.get();
    }

    public void touch(long timestamp) {
        lastSeenAt.set(timestamp);
    }

    public int getOutboundQueueSize() {
        return outboundQueueSize;
    }

    public void setOutboundQueueSize(int outboundQueueSize) {
        this.outboundQueueSize = outboundQueueSize;
    }
}
