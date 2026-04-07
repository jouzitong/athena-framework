package org.athena.framework.security.api.spi;

import org.athena.framework.security.api.model.MutableUserContext;

public interface UserContextEnricher {

    int order();

    void enrich(MutableUserContext context);
}
