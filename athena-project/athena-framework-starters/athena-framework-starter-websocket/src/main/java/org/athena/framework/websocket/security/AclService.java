package org.athena.framework.websocket.security;

public interface AclService {

    boolean canSubscribe(TokenInfo user, String topic);

    boolean canPublish(TokenInfo user, String topic);

    boolean canRequest(TokenInfo user, String action);
}
