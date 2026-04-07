package org.athena.framework.security.api.spi;

import org.athena.framework.security.api.model.UserContext;

public interface TokenManager {

    String create(UserContext context);

    UserContext parse(String token);

    void invalidate(String token);
}
