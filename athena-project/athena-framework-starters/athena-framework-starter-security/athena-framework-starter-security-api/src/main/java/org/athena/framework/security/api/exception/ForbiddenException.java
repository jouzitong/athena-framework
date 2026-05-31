package org.athena.framework.security.api.exception;

import org.arthena.framework.common.constant.ErrCodeConstant;
import org.arthena.framework.common.exception.base.BaseHttpRuntimeException;

/**
 * 无权限异常（HTTP 403）。
 *
 * @author zhouzhitong
 * @since 2026/4/13
 */
public class ForbiddenException extends BaseHttpRuntimeException {

    public ForbiddenException() {
        super(403, ErrCodeConstant.FORBIDDEN);
    }

    public ForbiddenException(Object... args) {
        super(403, ErrCodeConstant.FORBIDDEN, args);
    }

    public ForbiddenException(Throwable cause, Object... args) {
        super(403, ErrCodeConstant.FORBIDDEN, cause, args);
    }
}

