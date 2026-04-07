package org.athena.framework.security.auth.core.service;

import org.apache.commons.lang3.StringUtils;
import org.athena.framework.security.api.auth.AuthenticationRequest;
import org.athena.framework.security.api.auth.AuthenticationResult;
import org.athena.framework.security.api.auth.CredentialInput;
import org.athena.framework.security.api.auth.CredentialVerifyResult;
import org.athena.framework.security.api.model.AuthnState;
import org.athena.framework.security.api.model.MutableUserContext;
import org.athena.framework.security.api.model.SessionState;
import org.athena.framework.security.api.model.Subject;
import org.athena.framework.security.api.principal.SecurityUser;
import org.athena.framework.security.api.spi.Authenticator;
import org.athena.framework.security.api.spi.CredentialVerifier;
import org.athena.framework.security.api.spi.SecurityUserRepository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public class DefaultAuthenticator implements Authenticator {

    private static final String CREDENTIAL_TYPE_PASSWORD = "PASSWORD";

    private final SecurityUserRepository securityUserRepository;

    private final CredentialVerifier credentialVerifier;

    public DefaultAuthenticator(SecurityUserRepository securityUserRepository, CredentialVerifier credentialVerifier) {
        this.securityUserRepository = securityUserRepository;
        this.credentialVerifier = credentialVerifier;
    }

    @Override
    public AuthenticationResult authenticate(AuthenticationRequest request) {
        if (StringUtils.isBlank(request.username()) || StringUtils.isBlank(request.password())) {
            return AuthenticationResult.failed("INVALID_ARGUMENT", "username or password is empty");
        }

        Optional<SecurityUser> userOptional = securityUserRepository.findByUsername(request.username());
        if (userOptional.isEmpty()) {
            return AuthenticationResult.failed("USER_NOT_FOUND", "user not found");
        }

        SecurityUser user = userOptional.get();
        CredentialInput credentialInput = new CredentialInput(request.username(), request.password(),
            StringUtils.defaultIfBlank(request.credentialType(), CREDENTIAL_TYPE_PASSWORD));
        CredentialVerifyResult verifyResult = credentialVerifier.verify(user, credentialInput);
        if (!verifyResult.success()) {
            return AuthenticationResult.failed(verifyResult.code(), verifyResult.message());
        }

        MutableUserContext userContext = new MutableUserContext();
        userContext.setSubject(new Subject(user.userId(), user.username(), user.tenantId(), user.userType()));
        userContext.setAuthn(new AuthnState(true, credentialInput.credentialType(), Instant.now()));
        userContext.setSession(new SessionState(UUID.randomUUID().toString(), null, Instant.now(), null));
        userContext.getAttributes().put("displayName", user.displayName());
        // TODO 多租户隔离能力待实现，当前仅保留 tenantId 字段，不参与认证判定。

        return AuthenticationResult.success(userContext);
    }
}
