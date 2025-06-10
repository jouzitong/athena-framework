package org.arthena.framework.common.exception;

import org.arthena.framework.common.constant.CodeConstant;
import org.arthena.framework.common.exception.base.BaseRuntimeException;

import java.io.Serial;

/**
 * 用户安全校验失败异常类 - 404XX
 *
 * @author zhouzhitong
 * @since 2021/8/27
 */
public class BaseUserException extends BaseRuntimeException {

    @Serial
    private static final long serialVersionUID = -8993934800066999384L;

    public BaseUserException(Integer code) {
        super(code);
    }

}
