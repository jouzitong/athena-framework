package org.athena.framework.websocket.router;

import org.athena.framework.websocket.protocol.WsMessage;
import org.athena.framework.websocket.session.WsSession;

public interface WsRouter {

    void route(WsSession session, WsMessage message);
}
