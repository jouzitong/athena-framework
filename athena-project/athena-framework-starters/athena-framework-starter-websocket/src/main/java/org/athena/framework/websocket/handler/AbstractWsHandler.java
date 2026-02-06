package org.athena.framework.websocket.handler;

import java.util.HashMap;
import java.util.Map;
import org.athena.framework.websocket.gateway.WsOutbound;
import org.athena.framework.websocket.protocol.WsMessage;
import org.athena.framework.websocket.protocol.WsMessageFactory;
import org.athena.framework.websocket.protocol.WsPayloadKey;
import org.athena.framework.websocket.protocol.WsPayloadStatus;
import org.athena.framework.websocket.session.WsSession;

/**
 * 基础处理器：封装通用支持类型与回包逻辑
 */
public abstract class AbstractWsHandler implements WsHandler {

    private final String type;
    protected final WsMessageFactory messageFactory;
    protected final WsOutbound outbound;

    protected AbstractWsHandler(String type, WsMessageFactory messageFactory, WsOutbound outbound) {
        this.type = type;
        this.messageFactory = messageFactory;
        this.outbound = outbound;
    }

    @Override
    public boolean supports(String type) {
        return this.type.equals(type);
    }

    protected void sendOk(WsSession session, WsMessage request, Map<String, Object> payload) {
        Map<String, Object> body = payload == null ? new HashMap<>() : payload;
        body.putIfAbsent(WsPayloadKey.STATUS, WsPayloadStatus.OK);
        WsMessage response = messageFactory.okResponse(request, body);
        outbound.send(session, response);
    }

    protected void sendError(WsSession session, WsMessage request, String code, String message) {
        WsMessage response = messageFactory.errorResponse(request, code, message, false);
        outbound.send(session, response);
    }
}
