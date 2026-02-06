package org.athena.framework.websocket.gateway;

import org.athena.framework.websocket.protocol.WsMessage;
import org.athena.framework.websocket.session.WsSession;

/**
 * 出站发送接口
 */
public interface WsOutbound {

    /**
     * 向指定会话发送消息
     */
    void send(WsSession session, WsMessage message);
}
