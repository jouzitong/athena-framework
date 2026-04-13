package org.athena.framework.security.api.spi;

/**
 * token 解析状态。
 * <p>
 * 用于区分 token 缺失/过期/格式错误/签名错误等场景，便于输出更有参考价值的提示语。
 */
public enum TokenParseStatus {
    OK,
    EMPTY,
    INVALID_FORMAT,
    INVALID_SIGNATURE,
    EXPIRED,
    MISSING_CONTEXT,
    ERROR
}

