package org.athena.framework.websocket.gateway;

import org.springframework.web.socket.WebSocketSession;

public interface ConnectionRegistry {

    void add(String connId, WebSocketSession session);

    WebSocketSession get(String connId);

    void remove(String connId);
}
