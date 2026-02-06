package org.athena.framework.websocket.metrics;

public class NoopWsMetrics implements WsMetrics {

    @Override
    public void onConnectionOpen() {
        // 空实现
    }

    @Override
    public void onConnectionClosed(int closeCode) {
        // 空实现
    }

    @Override
    public void onHandshakeFailed() {
        // 空实现
    }

    @Override
    public void onInboundMessage(String type) {
        // 空实现
    }

    @Override
    public void onOutboundMessage(String type) {
        // 空实现
    }

    @Override
    public void onRouterError(String type) {
        // 空实现
    }
}
