package org.arthena.framework.common.exception.base;

import lombok.Getter;
import org.arthena.framework.common.constant.CodeConstant;

/**
 * @author zhouzhitong
 * @version 1.0
 * @since 2022/5/15 16:31
 */
public class BaseException extends Exception {

    @Getter
    private final Integer code;

    @Getter
    private final Object[] args;

    public BaseException(String message) {
        this(message, CodeConstant.UN_KNOW_ERROR);
    }

    public BaseException(Exception e) {
        super(e);
        if (e instanceof BaseException baseException) {
            this.code = baseException.getCode();
            this.args = baseException.args;
        } else {
            this.code = CodeConstant.UN_KNOW_ERROR;
            this.args = null;
        }
    }

    public BaseException(Integer code) {
        this(null, code);
    }

    public BaseException(String message, Integer code) {
        super(message);
        this.code = code;
        this.args = null;
    }

    public String getMsg() {
        return getMessage();
    }

    public Integer code() {
        return this.code;
    }

}
