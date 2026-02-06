package org.athena.framework.websocket.handler;

import org.athena.framework.websocket.gateway.WsOutbound;
import org.athena.framework.websocket.protocol.WsMessage;
import org.athena.framework.websocket.protocol.WsMessageFactory;
import org.athena.framework.websocket.session.WsSession;

public class PingHandler implements WsHandler {

    private final WsMessageFactory messageFactory;
    private final WsOutbound outbound;

    public PingHandler(WsMessageFactory messageFactory, WsOutbound outbound) {
        this.messageFactory = messageFactory;
        this.outbound = outbound;
    }

    @Override
    public boolean supports(String type) {
        return "PING".equals(type);
    }

    @Override
    public void handle(WsSession session, WsMessage message) {
        WsMessage pong = messageFactory.pong(message);
        outbound.send(session, pong);
    }
}
