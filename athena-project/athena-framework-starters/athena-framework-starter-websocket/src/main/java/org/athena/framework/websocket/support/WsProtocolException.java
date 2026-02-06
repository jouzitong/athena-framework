package org.athena.framework.websocket.support;

/**
 * 协议校验异常，携带错误码
 */
public class WsProtocolException extends RuntimeException {

    private final WsErrorCode code;

    public WsProtocolException(WsErrorCode code, String message) {
        super(message);
        this.code = code;
    }

    public WsErrorCode getCode() {
        return code;
    }
}
