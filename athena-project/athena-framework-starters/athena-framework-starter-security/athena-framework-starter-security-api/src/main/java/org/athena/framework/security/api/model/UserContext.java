package org.athena.framework.security.api.model;

import java.util.Map;

public interface UserContext {

    Subject subject();

    AuthnState authn();

    AuthorizationSnapshot authorization();

    SessionState session();

    Map<String, Object> attributes();
}
