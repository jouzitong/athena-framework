package org.athena.framework.security.api.event;

public record AuthenticationFailureEvent(String username, String code, String message) {
}
