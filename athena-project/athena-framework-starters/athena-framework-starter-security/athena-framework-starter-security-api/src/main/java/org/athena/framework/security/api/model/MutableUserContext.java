package org.athena.framework.security.api.model;

import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
/**
 * 可变用户上下文实现。
 * 在认证与鉴权过程中作为统一载体传递主体信息、认证态、授权态和扩展属性。
 */
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
