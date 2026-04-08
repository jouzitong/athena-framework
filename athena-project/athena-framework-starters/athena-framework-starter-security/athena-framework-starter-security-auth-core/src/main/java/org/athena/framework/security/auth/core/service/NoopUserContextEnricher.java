package org.athena.framework.security.auth.core.service;

import org.athena.framework.security.api.model.MutableUserContext;
import org.athena.framework.security.api.spi.UserContextEnricher;

/**
 * 空实现的用户上下文增强器。
 * 作为默认兜底 Bean，确保增强器链路始终可用。
 */
public class NoopUserContextEnricher implements UserContextEnricher {

    @Override
    public int order() {
        return 0;
    }

    @Override
    public void enrich(MutableUserContext context) {
        // default no-op
    }
}
