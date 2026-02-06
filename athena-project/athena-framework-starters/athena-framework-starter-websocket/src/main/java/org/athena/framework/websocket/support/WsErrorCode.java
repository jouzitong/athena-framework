package org.athena.framework.websocket.support;

public enum WsErrorCode {
    /**
     * 协议格式错误
     */
    BAD_SCHEMA,
    /**
     * 协议版本不支持
     */
    UNSUPPORTED_VERSION,
    /**
     * 无权限
     */
    NO_PERMISSION,
    /**
     * 触发限流
     */
    RATE_LIMITED,
    /**
     * Topic 不存在
     */
    TOPIC_NOT_FOUND,
    /**
     * 服务端异常
     */
    INTERNAL_ERROR,
    /**
     * 服务繁忙
     */
    BUSY,
    /**
     * 恢复 ID 不存在
     */
    RESUME_NOT_FOUND,
    /**
     * 恢复 ID 过期
     */
    RESUME_EXPIRED,
    /**
     * 恢复 ID 与用户不匹配
     */
    RESUME_FORBIDDEN,
    /**
     * 未知动作
     */
    UNKNOWN_ACTION
}
