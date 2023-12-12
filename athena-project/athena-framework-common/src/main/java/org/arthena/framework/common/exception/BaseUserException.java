package org.arthena.framework.common.exception;

import org.arthena.framework.common.constant.CodeConstant;

import java.io.Serial;

/**
 * 用户安全校验失败异常类 - 404XX
 *
 * @author zhouzhitong
 * @since 2021/8/27
 */
public class BaseUserException extends BaseException {

    @Serial
    private static final long serialVersionUID = -8993934800066999384L;

    public BaseUserException() {
        this(CodeConstant.USER_LOGIN_ERROR);
    }

    public BaseUserException(String message) {
        this(message, CodeConstant.USER_LOGIN_ERROR);
    }

    public BaseUserException(Code code) {
        this(code.desc(), code);
    }

    protected BaseUserException(String message, Code code) {
        super(message, code);
    }

}
