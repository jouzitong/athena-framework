package org.athena.framework.security.api.auth;

/**
 * 凭据校验结果。
 * 表示密码等凭据是否通过校验，并附带失败原因。
 */
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
