package org.athena.framework.security.api.auth;

/**
 * 凭据校验输入参数。
 * 在认证流程中用于向凭据校验器传递标准化用户名、凭据内容和凭据类型。
 */
public record CredentialInput(
    String username,
    String password,
    String credentialType
) {
}
