package org.athena.framework.security.auth.core.context;

import org.athena.framework.security.api.model.UserContext;

/**
 * 安全上下文持有器。
 * 基于 ThreadLocal 保存当前线程的用户上下文，过滤器在请求结束时负责清理。
 */
public final class SecurityContextHolder {

    private static final ThreadLocal<UserContext> CONTEXT = new ThreadLocal<>();

    private SecurityContextHolder() {
    }

    public static void set(UserContext userContext) {
        CONTEXT.set(userContext);
    }

    public static UserContext get() {
        return CONTEXT.get();
    }

    public static void clear() {
        CONTEXT.remove();
    }
}
