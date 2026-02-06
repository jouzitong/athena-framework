package org.athena.framework.websocket.bus;

import org.athena.framework.websocket.protocol.WsMessage;

/**
 * 主题分发器：将总线消息分发到本地订阅连接
 */
public interface TopicDispatcher {

    /**
     * 注册 topic 的分发
     */
    void register(String topic);

    /**
     * 主动分发消息
     */
    void dispatch(WsMessage message);
}
