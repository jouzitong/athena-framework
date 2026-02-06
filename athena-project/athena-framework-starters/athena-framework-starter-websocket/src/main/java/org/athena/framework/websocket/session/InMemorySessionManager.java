package org.athena.framework.websocket.session;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemorySessionManager implements SessionManager {

    private final Map<String, WsSession> sessions = new ConcurrentHashMap<>();

    @Override
    public void addSession(WsSession session) {
        if (session != null) {
            sessions.put(session.getConnId(), session);
        }
    }

    @Override
    public WsSession getSession(String connId) {
        return sessions.get(connId);
    }

    @Override
    public void removeSession(String connId) {
        if (connId != null) {
            sessions.remove(connId);
        }
    }

    @Override
    public Collection<WsSession> allSessions() {
        return Collections.unmodifiableCollection(sessions.values());
    }
}
