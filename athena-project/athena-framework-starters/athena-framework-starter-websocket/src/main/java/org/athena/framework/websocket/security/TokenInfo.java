package org.athena.framework.websocket.security;

import java.util.Collections;
import java.util.Map;

public class TokenInfo {

    private final String userId;
    private final Map<String, Object> claims;

    public TokenInfo(String userId, Map<String, Object> claims) {
        this.userId = userId;
        this.claims = claims == null ? Collections.emptyMap() : claims;
    }

    public String getUserId() {
        return userId;
    }

    public Map<String, Object> getClaims() {
        return claims;
    }
}
