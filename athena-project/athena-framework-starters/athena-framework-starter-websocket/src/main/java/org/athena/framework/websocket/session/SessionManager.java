package org.athena.framework.websocket.session;

import java.util.Collection;

/**
 * 会话管理接口
 */
public interface SessionManager {

    /**
     * 添加会话
     */
    void addSession(WsSession session);

    /**
     * 获取会话
     */
    WsSession getSession(String connId);

    /**
     * 移除会话
     */
    void removeSession(String connId);

    /**
     * 获取全部会话
     */
    Collection<WsSession> allSessions();
}
