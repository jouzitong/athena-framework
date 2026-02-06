package org.athena.framework.websocket.protocol;

import org.athena.framework.websocket.protocol.WsMessageType;
import org.athena.framework.websocket.protocol.WsPayloadKey;

import java.util.HashMap;
import java.util.Map;

/**
 * 标准消息构造器
 */
public class WsMessageFactory {

    public WsMessage pong(WsMessage ping) {
        WsMessage msg = new WsMessage();
        msg.setVersion(ping.getVersion());
        msg.setType(WsMessageType.PONG);
        msg.setTimestamp(System.currentTimeMillis());
        msg.setPayload(new HashMap<>());
        return msg;
    }

    public WsMessage okResponse(WsMessage request, Map<String, Object> payload) {
        WsMessage msg = new WsMessage();
        msg.setVersion(defaultVersion(request));
        msg.setType(WsMessageType.RESPONSE);
        msg.setRequestId(request.getRequestId());
        msg.setTopic(request.getTopic());
        msg.setTimestamp(System.currentTimeMillis());
        msg.setPayload(payload);
        return msg;
    }

    public WsMessage errorResponse(WsMessage request, String code, String message, boolean retryable) {
        WsMessage msg = new WsMessage();
        msg.setVersion(defaultVersion(request));
        msg.setType(WsMessageType.ERROR);
        msg.setRequestId(request.getRequestId());
        msg.setTopic(request.getTopic());
        msg.setTimestamp(System.currentTimeMillis());
        Map<String, Object> payload = new HashMap<>();
        payload.put(WsPayloadKey.CODE, code);
        payload.put(WsPayloadKey.MESSAGE, message);
        payload.put(WsPayloadKey.RETRYABLE, retryable);
        msg.setPayload(payload);
        return msg;
    }

    private String defaultVersion(WsMessage request) {
        // 请求为空时采用默认版本，避免 NPE
        if (request == null || request.getVersion() == null) {
            return "1.0";
        }
        return request.getVersion();
    }
}
