package org.athena.framework.websocket.backpressure;

public enum BackpressureStrategy {
    /**
     * 丢弃最旧消息，保留最新消息
     */
    DROP_OLD,
    /**
     * 丢弃最新消息，保留旧消息
     */
    DROP_NEW,
    /**
     * 断开慢连接（策略标识）
     */
    DISCONNECT_SLOW,
    /**
     * 按节流间隔发送（策略标识）
     */
    THROTTLE
}
