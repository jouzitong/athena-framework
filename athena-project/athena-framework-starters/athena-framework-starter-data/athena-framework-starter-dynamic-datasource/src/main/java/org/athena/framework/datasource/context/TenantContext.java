package org.athena.framework.datasource.context;

import org.arthena.framework.common.context.SystemContext;

/**
 * 租户上下文兼容入口。
 *
 * @deprecated 请改用 {@link SystemContext#setTenantId(String)}、
 * {@link SystemContext#getTenantId()}、
 * {@link SystemContext#clearTenantId()}。
 */
@Deprecated(since = "1.4.2", forRemoval = true)
public final class TenantContext {

    private TenantContext() {
    }

    /**
     * @deprecated 请改用 {@link SystemContext#setTenantId(String)}
     */
    @Deprecated(since = "1.4.2", forRemoval = true)
    public static void setTenantId(String tenantId) {
        SystemContext.setTenantId(tenantId);
    }

    /**
     * @deprecated 请改用 {@link SystemContext#getTenantId()}
     */
    @Deprecated(since = "1.4.2", forRemoval = true)
    public static String getTenantId() {
        return SystemContext.getTenantId();
    }

    /**
     * @deprecated 请改用 {@link SystemContext#clearTenantId()}
     */
    @Deprecated(since = "1.4.2", forRemoval = true)
    public static void clear() {
        SystemContext.clearTenantId();
    }
}
