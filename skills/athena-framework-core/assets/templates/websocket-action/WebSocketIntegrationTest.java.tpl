package {{PACKAGE}}.websocket;

import org.athena.framework.websocket.gateway.WsOutbound;
import org.athena.framework.websocket.protocol.WsMessage;
import org.athena.framework.websocket.protocol.WsMessageFactory;
import org.athena.framework.websocket.session.WsSession;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class {{NAME}}WsActionHandlerTest {

    @Test
    void handlesSupportedActionAndReplies() {
        WsOutbound outbound = mock(WsOutbound.class);
        WsMessageFactory factory = new WsMessageFactory();
        {{NAME}}WsActionHandler handler = new {{NAME}}WsActionHandler(
                (userId, payload) -> Map.of("owner", userId), factory, outbound);
        WsSession session = new WsSession("c1", "u1", Map.of(), "client", "resume", 1L);
        WsMessage request = new WsMessage();
        request.setVersion("1.0");
        request.setRequestId("r1");

        handler.handle(session, request);

        assertThat(handler.supports({{NAME}}WsActionHandler.ACTION)).isTrue();
        verify(outbound).send(eq(session), org.mockito.ArgumentMatchers.any(WsMessage.class));
    }
}
