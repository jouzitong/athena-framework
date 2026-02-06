package org.athena.framework.websocket.protocol;

import org.athena.framework.websocket.support.WsErrorCode;
import org.athena.framework.websocket.support.WsProtocolException;

public class MessageValidator {

    public void validate(WsMessage message) {
        if (message == null) {
            throw new WsProtocolException(WsErrorCode.BAD_SCHEMA, "message is null");
        }
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
        if (requiresRequestId(type) && isBlank(message.getRequestId())) {
            throw new WsProtocolException(WsErrorCode.BAD_SCHEMA, "requestId is required");
        }
        if (requiresTopic(type) && isBlank(message.getTopic())) {
            throw new WsProtocolException(WsErrorCode.BAD_SCHEMA, "topic is required");
        }
    }

    private boolean requiresRequestId(String type) {
        return "SUBSCRIBE".equals(type)
            || "UNSUBSCRIBE".equals(type)
            || "REQUEST".equals(type)
            || "RESPONSE".equals(type)
            || "ERROR".equals(type);
    }

    private boolean requiresTopic(String type) {
        return "SUBSCRIBE".equals(type)
            || "UNSUBSCRIBE".equals(type)
            || "REQUEST".equals(type)
            || "RESPONSE".equals(type)
            || "EVENT".equals(type)
            || "ERROR".equals(type);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
