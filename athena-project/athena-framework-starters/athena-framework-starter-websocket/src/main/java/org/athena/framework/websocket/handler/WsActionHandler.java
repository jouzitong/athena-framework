package org.athena.framework.websocket.handler;

import org.athena.framework.websocket.protocol.WsMessage;
import org.athena.framework.websocket.session.WsSession;

/**
 * Action 处理器接口
 */
public interface WsActionHandler {

    /**
     * 是否支持该 action
     */
    boolean supports(String action);

    /**
     * 处理 action
     */
    void handle(WsSession session, WsMessage message);
}
