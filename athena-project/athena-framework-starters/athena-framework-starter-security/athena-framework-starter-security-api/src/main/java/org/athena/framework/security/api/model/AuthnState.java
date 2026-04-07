package org.athena.framework.security.api.model;

import java.time.Instant;

public record AuthnState(
    boolean authenticated,
    String authType,
    Instant authenticatedAt
) {
}
