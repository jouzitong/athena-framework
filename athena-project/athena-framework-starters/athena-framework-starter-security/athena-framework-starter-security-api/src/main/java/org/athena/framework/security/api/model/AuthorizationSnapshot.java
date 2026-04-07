package org.athena.framework.security.api.model;

import java.util.Set;

public record AuthorizationSnapshot(
    Set<String> permissions,
    Set<String> roles,
    Set<String> scopes
) {
}
