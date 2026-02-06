package org.athena.framework.websocket.protocol;

import java.util.HashMap;
import java.util.Map;

public class WsMessageFactory {

    public WsMessage pong(WsMessage ping) {
        WsMessage msg = new WsMessage();
        msg.setVersion(ping.getVersion());
        msg.setType("PONG");
        msg.setTimestamp(System.currentTimeMillis());
        msg.setPayload(new HashMap<>());
        return msg;
    }

    public WsMessage okResponse(WsMessage request, Map<String, Object> payload) {
        WsMessage msg = new WsMessage();
        msg.setVersion(defaultVersion(request));
        msg.setType("RESPONSE");
        msg.setRequestId(request.getRequestId());
        msg.setTopic(request.getTopic());
        msg.setTimestamp(System.currentTimeMillis());
        msg.setPayload(payload);
        return msg;
    }

    public WsMessage errorResponse(WsMessage request, String code, String message, boolean retryable) {
        WsMessage msg = new WsMessage();
        msg.setVersion(defaultVersion(request));
        msg.setType("ERROR");
        msg.setRequestId(request.getRequestId());
        msg.setTopic(request.getTopic());
        msg.setTimestamp(System.currentTimeMillis());
        Map<String, Object> payload = new HashMap<>();
        payload.put("code", code);
        payload.put("message", message);
        payload.put("retryable", retryable);
        msg.setPayload(payload);
        return msg;
    }

    private String defaultVersion(WsMessage request) {
        if (request == null || request.getVersion() == null) {
            return "1.0";
        }
        return request.getVersion();
    }
}
