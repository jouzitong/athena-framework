package org.athena.framework.security.api.event;

import org.athena.framework.security.api.model.UserContext;

/**
 * 鉴权决策事件。
 * 在方法级权限校验后发布，供审计和监控模块消费。
 */
public record AuthorizationDecisionEvent(
    UserContext context,
    String resource,
    String[] permissions,
    boolean requireAll,
    boolean granted,
    String message
) {
}
