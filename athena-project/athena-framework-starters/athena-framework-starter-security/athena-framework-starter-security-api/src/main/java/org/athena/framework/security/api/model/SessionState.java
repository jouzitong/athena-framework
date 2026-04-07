package org.athena.framework.security.api.model;

import java.time.Instant;

public record SessionState(
    String sessionId,
    String tokenId,
    Instant issuedAt,
    Instant expireAt
) {
}
