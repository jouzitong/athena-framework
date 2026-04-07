package org.athena.framework.security.api.auth;

public record CredentialVerifyResult(
    boolean success,
    String code,
    String message
) {

    public static CredentialVerifyResult ok() {
        return new CredentialVerifyResult(true, "OK", "ok");
    }

    public static CredentialVerifyResult failed(String code, String message) {
        return new CredentialVerifyResult(false, code, message);
    }
}
