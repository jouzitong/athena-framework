package org.athena.framework.websocket.protocol;

import org.athena.framework.websocket.support.WsErrorCode;
import org.athena.framework.websocket.support.WsProtocolException;

/**
 * 协议消息校验器
 */
public class MessageValidator {

    public void validate(WsMessage message) {
        if (message == null) {
            throw new WsProtocolException(WsErrorCode.BAD_SCHEMA, "message is null");
        }
        // 基础字段校验
        if (isBlank(message.getVersion())) {
            throw new WsProtocolException(WsErrorCode.BAD_SCHEMA, "version is required");
        }
        if (isBlank(message.getType())) {
            throw new WsProtocolException(WsErrorCode.BAD_SCHEMA, "type is required");
        }
        if (message.getTimestamp() <= 0) {
            throw new WsProtocolException(WsErrorCode.BAD_SCHEMA, "timestamp is required");
        }
        String type = message.getType();
        // 按类型校验 requestId 与 topic
        if (requiresRequestId(type) && isBlank(message.getRequestId())) {
            throw new WsProtocolException(WsErrorCode.BAD_SCHEMA, "requestId is required");
        }
        if (requiresTopic(type) && isBlank(message.getTopic())) {
            throw new WsProtocolException(WsErrorCode.BAD_SCHEMA, "topic is required");
        }
    }

    private boolean requiresRequestId(String type) {
        return WsMessageType.SUBSCRIBE.equals(type)
            || WsMessageType.UNSUBSCRIBE.equals(type)
            || WsMessageType.REQUEST.equals(type)
            || WsMessageType.RESPONSE.equals(type)
            || WsMessageType.ERROR.equals(type);
    }

    private boolean requiresTopic(String type) {
        return WsMessageType.SUBSCRIBE.equals(type)
            || WsMessageType.UNSUBSCRIBE.equals(type)
            || WsMessageType.REQUEST.equals(type)
            || WsMessageType.RESPONSE.equals(type)
            || WsMessageType.EVENT.equals(type)
            || WsMessageType.ERROR.equals(type);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
