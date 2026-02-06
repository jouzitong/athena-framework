package org.athena.framework.websocket.support;

public enum WsErrorCode {
    BAD_SCHEMA,
    UNSUPPORTED_VERSION,
    NO_PERMISSION,
    RATE_LIMITED,
    TOPIC_NOT_FOUND,
    INTERNAL_ERROR,
    BUSY,
    RESUME_NOT_FOUND,
    RESUME_EXPIRED,
    RESUME_FORBIDDEN
}
