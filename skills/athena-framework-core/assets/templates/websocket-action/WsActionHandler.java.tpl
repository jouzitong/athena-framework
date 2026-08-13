package {{PACKAGE}}.websocket;

import org.athena.framework.websocket.gateway.WsOutbound;
import org.athena.framework.websocket.handler.WsActionHandler;
import org.athena.framework.websocket.protocol.WsMessage;
import org.athena.framework.websocket.protocol.WsMessageFactory;
import org.athena.framework.websocket.session.WsSession;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class {{NAME}}WsActionHandler implements WsActionHandler {

    public static final String ACTION = "{{NAME}}_QUERY";

    private final QueryPort queryPort;
    private final WsMessageFactory messageFactory;
    private final WsOutbound outbound;

    public {{NAME}}WsActionHandler(QueryPort queryPort, WsMessageFactory messageFactory, WsOutbound outbound) {
        this.queryPort = queryPort;
        this.messageFactory = messageFactory;
        this.outbound = outbound;
    }

    @Override
    public boolean supports(String action) {
        return ACTION.equals(action);
    }

    @Override
    public void handle(WsSession session, WsMessage message) {
        Map<String, Object> result = queryPort.query(session.getUserId(), message.getPayload());
        outbound.send(session, messageFactory.okResponse(message, result));
    }

    @FunctionalInterface
    public interface QueryPort {
        Map<String, Object> query(String userId, Object payload);
    }
}
