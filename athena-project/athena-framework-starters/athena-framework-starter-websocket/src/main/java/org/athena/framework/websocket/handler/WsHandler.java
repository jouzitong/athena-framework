package org.athena.framework.websocket.handler;

import org.athena.framework.websocket.protocol.WsMessage;
import org.athena.framework.websocket.session.WsSession;

/**
 * 消息处理器接口
 */
public interface WsHandler {

    /**
     * 是否支持该消息类型
     */
    boolean supports(String type);

    /**
     * 处理消息
     */
    void handle(WsSession session, WsMessage message);
}
