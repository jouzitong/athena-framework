package org.athena.framework.websocket.gateway;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.web.socket.WebSocketSession;

/**
 * 连接注册表的内存实现
 */
public class InMemoryConnectionRegistry implements ConnectionRegistry {

    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Override
    public void add(String connId, WebSocketSession session) {
        if (connId != null && session != null) {
            sessions.put(connId, session);
        }
    }

    @Override
    public WebSocketSession get(String connId) {
        return sessions.get(connId);
    }

    @Override
    public void remove(String connId) {
        if (connId != null) {
            sessions.remove(connId);
        }
    }
}
