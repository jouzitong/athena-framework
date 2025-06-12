package org.arthena.framework.common.exception.base;

/**
 * 业务异常
 *
 * @author zhouzhitong
 * @since 2025/6/10
 **/
public class BusinessRuntimeException extends BaseRuntimeException {

    public BusinessRuntimeException(Integer code, Object... args) {
        super(code, args);
    }

    public BusinessRuntimeException(Exception e) {
        super(e);
    }

}
