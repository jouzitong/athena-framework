package org.athena.framework.websocket.protocol;

/**
 * 消息类型常量
 */
public interface WsMessageType {
    String PING = "PING";
    String PONG = "PONG";
    String SUBSCRIBE = "SUBSCRIBE";
    String UNSUBSCRIBE = "UNSUBSCRIBE";
    String REQUEST = "REQUEST";
    String RESPONSE = "RESPONSE";
    String EVENT = "EVENT";
    String ACK = "ACK";
    String ERROR = "ERROR";
    String HELLO = "HELLO";
}
