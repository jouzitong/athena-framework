package org.arthena.framework.common.exception;

import org.arthena.framework.common.exception.base.BaseRuntimeException;

/**
 * 参数异常
 *
 * @author zhouzhitong
 * @since 2025/6/10
 **/
public class ArgumentException extends BaseRuntimeException {

    public ArgumentException(Exception e) {
        super(e);
    }

    public ArgumentException(Integer code) {
        super(code);
    }
}
