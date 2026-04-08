package org.athena.framework.security.api.model;

import java.time.Instant;

/**
 * 认证态快照。
 * 记录当前用户是否已认证、认证类型以及认证时间。
 */
public record AuthnState(
    boolean authenticated,
    String authType,
    Instant authenticatedAt
) {
}
