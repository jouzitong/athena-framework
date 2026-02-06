package org.athena.framework.websocket.metrics;

/**
 * 指标埋点接口
 */
public interface WsMetrics {

    /**
     * 连接建立
     */
    void onConnectionOpen();

    /**
     * 连接关闭
     */
    void onConnectionClosed(int closeCode);

    /**
     * 握手失败
     */
    void onHandshakeFailed();

    /**
     * 入站消息统计
     */
    void onInboundMessage(String type);

    /**
     * 出站消息统计
     */
    void onOutboundMessage(String type);

    /**
     * 路由层异常统计
     */
    void onRouterError(String type);
}
