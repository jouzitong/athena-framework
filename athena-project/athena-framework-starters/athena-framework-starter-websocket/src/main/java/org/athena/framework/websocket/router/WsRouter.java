package org.athena.framework.websocket.router;

import org.athena.framework.websocket.protocol.WsMessage;
import org.athena.framework.websocket.session.WsSession;

/**
 * 路由入口
 */
public interface WsRouter {

    /**
     * 路由分发入口
     */
    void route(WsSession session, WsMessage message);
}
