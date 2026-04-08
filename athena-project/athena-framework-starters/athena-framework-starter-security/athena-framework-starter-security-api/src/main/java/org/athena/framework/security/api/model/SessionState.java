package org.athena.framework.security.api.model;

import java.time.Instant;

/**
 * 会话态快照。
 * 描述当前请求绑定的会话标识、令牌及签发/过期时间。
 */
public record SessionState(
    String sessionId,
    String tokenId,
    Instant issuedAt,
    Instant expireAt
) {
}
