package org.athena.framework.security.api.spi;

import lombok.Getter;
import org.athena.framework.security.api.model.UserContext;

/**
 * token 解析结果。
 */
@Getter
public class TokenParseResult {

    private final UserContext userContext;

    private final TokenParseStatus status;

    public TokenParseResult(UserContext userContext, TokenParseStatus status) {
        this.userContext = userContext;
        this.status = status == null ? TokenParseStatus.ERROR : status;
    }

}

