package org.athena.framework.security.auth.core.token;

import org.athena.framework.security.api.model.UserContext;
import org.athena.framework.security.api.spi.TokenManager;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class LocalTokenManager implements TokenManager {

    private final Map<String, UserContext> tokenStore = new ConcurrentHashMap<>();

    @Override
    public String create(UserContext context) {
        String token = UUID.randomUUID().toString();
        tokenStore.put(token, context);
        return token;
    }

    @Override
    public UserContext parse(String token) {
        return tokenStore.get(token);
    }

    @Override
    public void invalidate(String token) {
        tokenStore.remove(token);
    }
}
