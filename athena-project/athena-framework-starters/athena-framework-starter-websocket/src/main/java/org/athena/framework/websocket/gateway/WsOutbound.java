package org.athena.framework.websocket.gateway;

import org.athena.framework.websocket.protocol.WsMessage;
import org.athena.framework.websocket.session.WsSession;

public interface WsOutbound {

    void send(WsSession session, WsMessage message);
}
