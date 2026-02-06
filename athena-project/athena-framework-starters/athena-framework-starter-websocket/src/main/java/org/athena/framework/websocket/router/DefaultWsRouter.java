package org.athena.framework.websocket.router;

import java.util.List;
import java.util.Map;
import org.athena.framework.websocket.handler.WsHandler;
import org.athena.framework.websocket.metrics.WsMetrics;
import org.athena.framework.websocket.protocol.WsMessage;
import org.athena.framework.websocket.security.AclService;
import org.athena.framework.websocket.security.TokenInfo;
import org.athena.framework.websocket.session.WsSession;
import org.athena.framework.websocket.support.WsErrorCode;
import org.athena.framework.websocket.support.WsProtocolException;

public class DefaultWsRouter implements WsRouter {

    private final List<WsHandler> handlers;
    private final AclService aclService;
    private final WsMetrics metrics;

    public DefaultWsRouter(List<WsHandler> handlers, AclService aclService, WsMetrics metrics) {
        this.handlers = handlers;
        this.aclService = aclService;
        this.metrics = metrics;
    }

    @Override
    public void route(WsSession session, WsMessage message) {
        if (session == null || message == null) {
            throw new WsProtocolException(WsErrorCode.BAD_SCHEMA, "session or message is null");
        }
        TokenInfo tokenInfo = new TokenInfo(session.getUserId(), session.getClaims());
        String type = message.getType();
        if ("SUBSCRIBE".equals(type) || "UNSUBSCRIBE".equals(type)) {
            if (!aclService.canSubscribe(tokenInfo, message.getTopic())) {
                throw new WsProtocolException(WsErrorCode.NO_PERMISSION, "no permission to subscribe");
            }
        }
        if ("REQUEST".equals(type)) {
            String action = extractAction(message.getPayload());
            if (!aclService.canRequest(tokenInfo, action)) {
                throw new WsProtocolException(WsErrorCode.NO_PERMISSION, "no permission to request");
            }
        }
        WsHandler handler = findHandler(type);
        if (handler == null) {
            throw new WsProtocolException(WsErrorCode.BAD_SCHEMA, "unsupported type: " + type);
        }
        try {
            handler.handle(session, message);
        } catch (RuntimeException ex) {
            metrics.onRouterError(type);
            throw ex;
        }
    }

    private WsHandler findHandler(String type) {
        for (WsHandler handler : handlers) {
            if (handler.supports(type)) {
                return handler;
            }
        }
        return null;
    }

    private String extractAction(Object payload) {
        if (payload instanceof Map) {
            Object action = ((Map<?, ?>) payload).get("action");
            if (action != null) {
                return String.valueOf(action);
            }
        }
        return "";
    }
}
