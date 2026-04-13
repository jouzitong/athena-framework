package org.athena.framework.security.api.spi;

import org.athena.framework.security.api.model.UserContext;

/**
 * token 解析结果。
 */
public class TokenParseResult {

    private final UserContext userContext;

    private final TokenParseStatus status;

    public TokenParseResult(UserContext userContext, TokenParseStatus status) {
        this.userContext = userContext;
        this.status = status == null ? TokenParseStatus.ERROR : status;
    }

    public UserContext getUserContext() {
        return userContext;
    }

    public TokenParseStatus getStatus() {
        return status;
    }
}

