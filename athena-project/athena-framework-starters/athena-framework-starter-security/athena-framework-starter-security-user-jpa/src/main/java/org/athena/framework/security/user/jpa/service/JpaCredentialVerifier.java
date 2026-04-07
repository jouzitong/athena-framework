package org.athena.framework.security.user.jpa.service;

import org.apache.commons.lang3.StringUtils;
import org.athena.framework.security.api.auth.CredentialInput;
import org.athena.framework.security.api.auth.CredentialVerifyResult;
import org.athena.framework.security.api.principal.SecurityUser;
import org.athena.framework.security.api.spi.CredentialVerifier;
import org.springframework.security.crypto.password.PasswordEncoder;

public class JpaCredentialVerifier implements CredentialVerifier {

    private final PasswordEncoder passwordEncoder;

    public JpaCredentialVerifier(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public CredentialVerifyResult verify(SecurityUser user, CredentialInput input) {
        if (!StringUtils.equalsIgnoreCase(input.credentialType(), "PASSWORD")) {
            return CredentialVerifyResult.failed("UNSUPPORTED_CREDENTIAL_TYPE", "unsupported credential type");
        }
        if (!StringUtils.equalsIgnoreCase(user.status(), "ENABLED")) {
            return CredentialVerifyResult.failed("USER_DISABLED", "user is disabled");
        }
        if (StringUtils.isBlank(user.passwordHash())) {
            return CredentialVerifyResult.failed("CREDENTIAL_NOT_FOUND", "password hash is empty");
        }

        boolean matched = passwordEncoder.matches(input.password(), user.passwordHash())
            || StringUtils.equals(user.passwordHash(), input.password());
        return matched ? CredentialVerifyResult.ok() : CredentialVerifyResult.failed("BAD_CREDENTIAL", "bad credential");
    }
}
