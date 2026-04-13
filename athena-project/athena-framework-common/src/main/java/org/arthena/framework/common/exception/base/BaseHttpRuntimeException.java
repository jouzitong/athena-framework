package org.arthena.framework.common.exception.base;

/**
 * 带 HTTP 状态码语义的运行时异常基类。
 * <p>
 * 说明：
 * <ul>
 *     <li>该类不依赖 Servlet/Spring 类型，仅以 int 表达 HTTP status。</li>
 *     <li>web 模块可根据 {@link #getHttpStatus()} 决定响应状态码。</li>
 * </ul>
 *
 * @author zhouzhitong
 * @since 2026/4/13
 */
public class BaseHttpRuntimeException extends BaseRuntimeException {

    private final int httpStatus;

    public BaseHttpRuntimeException(int httpStatus, Exception e) {
        super(e);
        this.httpStatus = httpStatus;
    }

    public BaseHttpRuntimeException(int httpStatus, Integer code, Object... args) {
        super(code, args);
        this.httpStatus = httpStatus;
    }

    public BaseHttpRuntimeException(int httpStatus, Integer code, Throwable cause, Object... args) {
        super(code, args);
        this.httpStatus = httpStatus;
        if (cause != null) {
            initCause(cause);
        }
    }

    public int getHttpStatus() {
        return httpStatus;
    }
}

