package org.athena.framework.security.auth.core.context;

import org.arthena.framework.common.context.SystemContext;
import org.athena.framework.security.api.model.Subject;
import org.athena.framework.security.api.model.UserContext;

/**
 * 安全上下文持有器。
 *
 * @deprecated 请改用 {@link SystemContext#setUserContext(Object)}、
 * {@link SystemContext#getUserContext()}、
 * {@link SystemContext#clearUserContext()}。
 */
@Deprecated(since = "1.4.2", forRemoval = true)
public final class SecurityContextHolder {

    private SecurityContextHolder() {
    }

    /**
     * @deprecated 请改用 {@link SystemContext#setUserContext(Object)}
     */
    @Deprecated(since = "1.4.2", forRemoval = true)
    public static void set(UserContext userContext) {
        SystemContext.setUserContext(userContext);
    }

    /**
     * @deprecated 请改用 {@link SystemContext#getUserContext()}
     */
    @Deprecated(since = "1.4.2", forRemoval = true)
    public static UserContext get() {
        return SystemContext.getUserContext();
    }

    /**
     * @deprecated 请改用 {@link SystemContext#getUserContext()}
     */
    @Deprecated(since = "1.4.2", forRemoval = true)
    public static Subject getSubject() {
        UserContext userContext = get();
        return userContext == null ? null : userContext.subject();
    }

    /**
     * @deprecated 请改用 {@link SystemContext#clearUserContext()}
     */
    @Deprecated(since = "1.4.2", forRemoval = true)
    public static void clear() {
        SystemContext.clearUserContext();
    }
}
