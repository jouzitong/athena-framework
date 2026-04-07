package org.athena.framework.security.api.model;

import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
public class MutableUserContext implements UserContext {

    private Subject subject;

    private AuthnState authn;

    private AuthorizationSnapshot authorization;

    private SessionState session;

    private final Map<String, Object> attributes = new LinkedHashMap<>();

    @Override
    public Subject subject() {
        return subject;
    }

    @Override
    public AuthnState authn() {
        return authn;
    }

    @Override
    public AuthorizationSnapshot authorization() {
        return authorization;
    }

    @Override
    public SessionState session() {
        return session;
    }

    @Override
    public Map<String, Object> attributes() {
        return attributes;
    }
}
