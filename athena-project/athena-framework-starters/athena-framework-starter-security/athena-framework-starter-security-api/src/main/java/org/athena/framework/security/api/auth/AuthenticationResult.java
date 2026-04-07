package org.athena.framework.security.api.auth;

import org.athena.framework.security.api.model.MutableUserContext;

public record AuthenticationResult(
    boolean success,
    String code,
    String message,
    MutableUserContext context
) {

    public static AuthenticationResult success(MutableUserContext context) {
        return new AuthenticationResult(true, "OK", "ok", context);
    }

    public static AuthenticationResult failed(String code, String message) {
        return new AuthenticationResult(false, code, message, null);
    }
}
