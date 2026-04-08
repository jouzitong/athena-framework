package org.athena.framework.security.api.event;

/**
 * 认证失败事件。
 * 用于向审计、监控等监听器广播失败用户名和失败原因。
 */
public record AuthenticationFailureEvent(String username, String code, String message) {
}
