package org.athena.framework.websocket.metrics;

public interface WsMetrics {

    void onConnectionOpen();

    void onConnectionClosed(int closeCode);

    void onHandshakeFailed();

    void onInboundMessage(String type);

    void onOutboundMessage(String type);

    void onRouterError(String type);
}
