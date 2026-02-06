package org.athena.framework.websocket.metrics;

public class NoopWsMetrics implements WsMetrics {

    @Override
    public void onConnectionOpen() {
    }

    @Override
    public void onConnectionClosed(int closeCode) {
    }

    @Override
    public void onHandshakeFailed() {
    }

    @Override
    public void onInboundMessage(String type) {
    }

    @Override
    public void onOutboundMessage(String type) {
    }

    @Override
    public void onRouterError(String type) {
    }
}
