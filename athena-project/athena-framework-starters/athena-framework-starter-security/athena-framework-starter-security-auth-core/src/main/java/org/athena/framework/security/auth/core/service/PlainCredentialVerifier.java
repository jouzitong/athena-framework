package org.athena.framework.security.auth.core.service;

import org.apache.commons.lang3.StringUtils;
import org.athena.framework.security.api.auth.CredentialInput;
import org.athena.framework.security.api.auth.CredentialVerifyResult;
import org.athena.framework.security.api.principal.SecurityUser;
import org.athena.framework.security.api.spi.CredentialVerifier;

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
