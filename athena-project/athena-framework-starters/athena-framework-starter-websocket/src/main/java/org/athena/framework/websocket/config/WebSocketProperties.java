package org.athena.framework.websocket.config;

import org.athena.framework.websocket.backpressure.BackpressureStrategy;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * WebSocket 配置项
 */
@ConfigurationProperties(prefix = "athena.websocket")
public class WebSocketProperties {

    /**
     * WebSocket 端点路径
     */
    private String endpoint = "/ws/v1";
    /**
     * 单实例最大连接数
     */
    private int maxConnections = 50_000;
    /**
     * 单条消息最大字节数
     */
    private int maxMessageBytes = 1_048_576;
    /**
     * 单帧最大字节数
     */
    private int maxFrameBytes = 1_048_576;
    /**
     * 单连接入站消息速率限制
     */
    private int inboundMsgRateLimitPerConn = 50;
    /**
     * 握手速率限制
     */
    private int handshakeRateLimit = 100;
    /**
     * 空闲超时
     */
    private long idleTimeoutMs = 60_000;
    /**
     * 心跳间隔
     */
    private long heartbeatIntervalMs = 20_000;
    /**
     * 单连接出站队列最大长度
     */
    private int maxOutboundQueuePerConn = 1_000;
    /**
     * 慢连接断开阈值
     */
    private int slowConnDisconnectThreshold = 5_000;
    /**
     * 订阅默认节流
     */
    private long topicThrottleDefaultMs = 200;
    /**
     * 断线恢复窗口
     */
    private long resumeTtlMs = 300_000;
    /**
     * 背压策略
     */
    private BackpressureStrategy backpressureStrategy = BackpressureStrategy.DROP_OLD;

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public int getMaxConnections() {
        return maxConnections;
    }

    public void setMaxConnections(int maxConnections) {
        this.maxConnections = maxConnections;
    }

    public int getMaxMessageBytes() {
        return maxMessageBytes;
    }

    public void setMaxMessageBytes(int maxMessageBytes) {
        this.maxMessageBytes = maxMessageBytes;
    }

    public int getMaxFrameBytes() {
        return maxFrameBytes;
    }

    public void setMaxFrameBytes(int maxFrameBytes) {
        this.maxFrameBytes = maxFrameBytes;
    }

    public int getInboundMsgRateLimitPerConn() {
        return inboundMsgRateLimitPerConn;
    }

    public void setInboundMsgRateLimitPerConn(int inboundMsgRateLimitPerConn) {
        this.inboundMsgRateLimitPerConn = inboundMsgRateLimitPerConn;
    }

    public int getHandshakeRateLimit() {
        return handshakeRateLimit;
    }

    public void setHandshakeRateLimit(int handshakeRateLimit) {
        this.handshakeRateLimit = handshakeRateLimit;
    }

    public long getIdleTimeoutMs() {
        return idleTimeoutMs;
    }

    public void setIdleTimeoutMs(long idleTimeoutMs) {
        this.idleTimeoutMs = idleTimeoutMs;
    }

    public long getHeartbeatIntervalMs() {
        return heartbeatIntervalMs;
    }

    public void setHeartbeatIntervalMs(long heartbeatIntervalMs) {
        this.heartbeatIntervalMs = heartbeatIntervalMs;
    }

    public int getMaxOutboundQueuePerConn() {
        return maxOutboundQueuePerConn;
    }

    public void setMaxOutboundQueuePerConn(int maxOutboundQueuePerConn) {
        this.maxOutboundQueuePerConn = maxOutboundQueuePerConn;
    }

    public int getSlowConnDisconnectThreshold() {
        return slowConnDisconnectThreshold;
    }

    public void setSlowConnDisconnectThreshold(int slowConnDisconnectThreshold) {
        this.slowConnDisconnectThreshold = slowConnDisconnectThreshold;
    }

    public long getTopicThrottleDefaultMs() {
        return topicThrottleDefaultMs;
    }

    public void setTopicThrottleDefaultMs(long topicThrottleDefaultMs) {
        this.topicThrottleDefaultMs = topicThrottleDefaultMs;
    }

    public long getResumeTtlMs() {
        return resumeTtlMs;
    }

    public void setResumeTtlMs(long resumeTtlMs) {
        this.resumeTtlMs = resumeTtlMs;
    }

    public BackpressureStrategy getBackpressureStrategy() {
        return backpressureStrategy;
    }

    public void setBackpressureStrategy(BackpressureStrategy backpressureStrategy) {
        this.backpressureStrategy = backpressureStrategy;
    }
}
