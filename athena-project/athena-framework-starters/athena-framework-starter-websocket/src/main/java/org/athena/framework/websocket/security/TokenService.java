package org.athena.framework.websocket.security;

public interface TokenService {

    TokenInfo parse(String token);
}
