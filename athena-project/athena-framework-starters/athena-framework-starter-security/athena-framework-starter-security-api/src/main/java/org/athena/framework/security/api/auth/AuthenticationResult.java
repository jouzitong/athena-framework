package org.athena.framework.security.api.auth;

import org.athena.framework.security.api.model.MutableUserContext;

/**
 * 认证结果对象。
 * 统一承载认证是否成功、错误码、错误信息以及认证后的用户上下文。
 */
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
