package org.athena.framework.security.api.model;

import org.athena.framework.security.api.spi.TokenParseStatus;

/**
 *
 * @author zhouzhitong
 * @since 2026/6/6
 */
public record TokenContext(String token, TokenParseStatus status, UserContext userContext) {

    public boolean authenticated() {
        return status == TokenParseStatus.OK && userContext != null;
    }
}
