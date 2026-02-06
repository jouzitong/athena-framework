package org.athena.framework.websocket.handler;

import org.athena.framework.websocket.protocol.WsMessage;
import org.athena.framework.websocket.session.WsSession;

public interface WsActionHandler {

    boolean supports(String action);

    void handle(WsSession session, WsMessage message);
}
