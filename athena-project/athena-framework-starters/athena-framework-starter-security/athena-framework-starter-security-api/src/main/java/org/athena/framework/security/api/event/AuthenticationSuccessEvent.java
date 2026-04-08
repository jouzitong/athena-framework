package org.athena.framework.security.api.event;

import org.athena.framework.security.api.model.UserContext;

/**
 * 认证成功事件。
 * 在登录成功后发布，供审计和后置扩展使用。
 */
public record AuthenticationSuccessEvent(UserContext context) {
}
