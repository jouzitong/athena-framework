package org.arthena.framework.common.exception.base;

import org.arthena.framework.common.constant.CodeConstant;
import lombok.Getter;
import org.arthena.framework.common.utils.ErrorCodeUtils;

/**
 * @author zhouzhitong
 * @version 1.0
 * @since 2022/5/15 16:31
 */
public class BaseRuntimeException extends RuntimeException {

    @Getter
    private final Integer code;

    @Getter
    private final Object[] args;

    public BaseRuntimeException(Exception e) {
        super(e);
        if (e instanceof BaseRuntimeException baseException) {
            this.code = baseException.getCode();
            this.args = baseException.args;
        } else {
            this.code = CodeConstant.UN_KNOW_ERROR;
            this.args = null;
        }
    }

    public BaseRuntimeException(Integer code) {
        super(ErrorCodeUtils.getMsg(code));
        this.code = code;
        this.args = null;
    }

    public BaseRuntimeException(Integer code, Object... args) {
        super(ErrorCodeUtils.getMsg(code, args));
        this.code = code;
        this.args = args;
    }

    public String getMsg() {
        return ErrorCodeUtils.getMsg(code, args);
    }

    public Integer code() {
        return this.code;
    }

}
