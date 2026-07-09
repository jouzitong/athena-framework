package org.athena.framework.security.api.exception;

import org.arthena.framework.common.constant.ErrCodeConstant;
import org.arthena.framework.common.exception.BizException;

/**
 * 无权限异常（HTTP 403）。
 *
 * @author zhouzhitong
 * @since 2026/4/13
 */
public class ForbiddenException extends BizException {

    public ForbiddenException() {
        super(ErrCodeConstant.FORBIDDEN);
        setStatus(403);
    }

    public ForbiddenException(Object... args) {
        super(ErrCodeConstant.FORBIDDEN, args);
        setStatus(403);
    }

    public ForbiddenException(Throwable cause, Object... args) {
        super(ErrCodeConstant.FORBIDDEN, cause, args);
        setStatus(403);
    }
}
