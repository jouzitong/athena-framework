package org.arthena.framework.common.exception.base;

/**
 * @author zhouzhitong
 * @since 2025/7/13
 **/
public record Code(int code, String message, Object... args) {
}
