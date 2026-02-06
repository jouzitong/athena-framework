package org.athena.framework.websocket.gateway;

import org.springframework.web.socket.WebSocketSession;

/**
 * 连接注册表
 */
public interface ConnectionRegistry {

    /**
     * 注册连接
     */
    void add(String connId, WebSocketSession session);

    /**
     * 获取连接
     */
    WebSocketSession get(String connId);

    /**
     * 移除连接
     */
    void remove(String connId);
}
