package org.arthena.framework.common.exception;

import org.arthena.framework.common.exception.base.BaseRuntimeException;

/**
 * 业务异常
 *
 * @author zhouzhitong
 * @since 2025/6/10
 **/
public class BusinessRuntimeException extends BaseRuntimeException {

    public BusinessRuntimeException(Integer code) {
        super(code);
    }

    public BusinessRuntimeException(Exception e) {
        super(e);
    }

}
