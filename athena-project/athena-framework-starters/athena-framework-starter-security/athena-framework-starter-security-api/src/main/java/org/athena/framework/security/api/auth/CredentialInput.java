package org.athena.framework.security.api.auth;

public record CredentialInput(
    String username,
    String password,
    String credentialType
) {
}
