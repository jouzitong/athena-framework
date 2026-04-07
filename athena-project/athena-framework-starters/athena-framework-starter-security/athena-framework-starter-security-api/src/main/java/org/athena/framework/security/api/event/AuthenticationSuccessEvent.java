package org.athena.framework.security.api.event;

import org.athena.framework.security.api.model.UserContext;

public record AuthenticationSuccessEvent(UserContext context) {
}
