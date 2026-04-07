package org.athena.framework.security.api.spi;

import org.athena.framework.security.api.model.UserContext;

public interface PermissionEvaluator {

    boolean hasPermission(UserContext userContext, String permission);
}
