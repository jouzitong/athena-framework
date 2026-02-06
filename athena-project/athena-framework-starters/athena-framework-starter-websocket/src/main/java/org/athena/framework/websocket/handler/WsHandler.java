package org.athena.framework.websocket.handler;

import org.athena.framework.websocket.protocol.WsMessage;
import org.athena.framework.websocket.session.WsSession;

public interface WsHandler {

    boolean supports(String type);

    void handle(WsSession session, WsMessage message);
}
