package org.athena.framework.websocket.session;

import java.util.Collection;

public interface SessionManager {

    void addSession(WsSession session);

    WsSession getSession(String connId);

    void removeSession(String connId);

    Collection<WsSession> allSessions();
}
