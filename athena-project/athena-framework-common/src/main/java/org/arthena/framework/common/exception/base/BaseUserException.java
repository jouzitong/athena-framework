package org.arthena.framework.common.exception.base;

import org.arthena.framework.common.constant.CodeConstant;

import java.io.Serial;

/**
 * 用户安全校验失败异常类 - 404XX
 *
 * @author zhouzhitong
 * @since 2021/8/27
 */
public class BaseUserException extends BaseHttpRuntimeException {

    @Serial
    private static final long serialVersionUID = -8993934800066999384L;

    public BaseUserException(Exception e) {
        super(401, e);
    }

    public BaseUserException(Integer code, Object... args) {
        super(401, code == null ? CodeConstant.UN_KNOW_ERROR : code, args);
    }

}
