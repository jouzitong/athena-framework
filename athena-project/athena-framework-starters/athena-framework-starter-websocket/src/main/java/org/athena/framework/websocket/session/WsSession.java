package org.athena.framework.websocket.session;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 连接会话对象
 */
public class WsSession {

    /**
     * 连接 ID
     */
    private final String connId;
    /**
     * 用户 ID
     */
    private final String userId;
    /**
     * 鉴权声明
     */
    private final Map<String, Object> claims;
    /**
     * 客户端稳定标识
     */
    private String clientId;
    /**
     * 会话恢复 ID
     */
    private String resumeId;
    /**
     * 连接创建时间
     */
    private final long createdAt;
    /**
     * 最近活跃时间
     */
    private final AtomicLong lastSeenAt = new AtomicLong();
    /**
     * 出站队列积压大小
     */
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
