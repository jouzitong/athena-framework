package org.athena.framework.security.api.auth;

/**
 * 认证请求参数。
 * 封装登录时输入的身份信息、凭据类型、租户及客户端来源信息。
 */
public record AuthenticationRequest(
    String username,
    String password,
    String tenantId,
    String credentialType,
    String clientIp
) {
}
