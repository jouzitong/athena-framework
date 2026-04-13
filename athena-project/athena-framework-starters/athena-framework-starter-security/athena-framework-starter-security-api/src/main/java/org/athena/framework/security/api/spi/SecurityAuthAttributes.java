package org.athena.framework.security.api.spi;

/**
 * 安全模块在请求上下文中写入的属性 Key。
 */
public interface SecurityAuthAttributes {

    /**
     * token 解析状态（{@link TokenParseStatus}）。
     */
    String TOKEN_PARSE_STATUS = "athena.security.token.parse.status";
}

