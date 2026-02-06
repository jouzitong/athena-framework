package org.athena.framework.websocket.handler;

import org.athena.framework.websocket.gateway.WsOutbound;
import org.athena.framework.websocket.protocol.WsMessage;
import org.athena.framework.websocket.protocol.WsMessageFactory;
import org.athena.framework.websocket.protocol.WsMessageType;
import org.athena.framework.websocket.session.WsSession;

/**
 * 心跳处理器
 */
public class PingHandler extends AbstractWsHandler {

    public PingHandler(WsMessageFactory messageFactory, WsOutbound outbound) {
        super(WsMessageType.PING, messageFactory, outbound);
    }

    @Override
    public void handle(WsSession session, WsMessage message) {
        // 心跳响应
        WsMessage pong = messageFactory.pong(message);
        outbound.send(session, pong);
    }
}
