package org.athena.framework.security.auth.core.service;

import org.athena.framework.security.api.model.MutableUserContext;
import org.athena.framework.security.api.spi.UserContextEnricher;

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
