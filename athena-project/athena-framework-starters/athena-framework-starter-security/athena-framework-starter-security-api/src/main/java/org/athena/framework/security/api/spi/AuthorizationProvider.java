package org.athena.framework.security.api.spi;

import java.util.Set;

public interface AuthorizationProvider {

    Set<String> permissions(String userId, String tenantId);
}
