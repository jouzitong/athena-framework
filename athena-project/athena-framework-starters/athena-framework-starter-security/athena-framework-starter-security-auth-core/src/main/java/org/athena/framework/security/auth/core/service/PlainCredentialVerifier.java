package org.athena.framework.security.auth.core.service;

import org.apache.commons.lang3.StringUtils;
import org.athena.framework.security.api.auth.CredentialInput;
import org.athena.framework.security.api.auth.CredentialVerifyResult;
import org.athena.framework.security.api.principal.SecurityUser;
import org.athena.framework.security.api.spi.CredentialVerifier;

/**
 * 明文凭据校验器。
 * 仅用于本地开发或测试场景，生产环境建议替换为加密哈希校验实现。
 */
public class PlainCredentialVerifier implements CredentialVerifier {

    @Override
    public CredentialVerifyResult verify(SecurityUser user, CredentialInput input) {
        if (user.passwordHash() == null) {
            return CredentialVerifyResult.failed("USER_CREDENTIAL_NOT_CONFIGURED", "credential not configured");
        }
        boolean matched = StringUtils.equals(user.passwordHash(), input.password());
        return matched ? CredentialVerifyResult.ok() : CredentialVerifyResult.failed("BAD_CREDENTIAL", "bad credential");
    }
}
