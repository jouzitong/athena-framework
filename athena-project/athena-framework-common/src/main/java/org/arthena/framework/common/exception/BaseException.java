package org.arthena.framework.common.exception;

import org.arthena.framework.common.constant.CodeConstant;
import lombok.Getter;

/**
 * @author zhouzhitong
 * @version 1.0
 * @since 2022/5/15 16:31
 */
public class BaseException extends RuntimeException {

    @Getter
    private final Code code;

    public BaseException(String message) {
        this(message, CodeConstant.UN_KNOW_ERROR);
    }

    public BaseException(Exception e) {
        super(e);
        if (e instanceof BaseException) {
            BaseException baseException = (BaseException) e;
            this.code = baseException.getCode();
        } else {
            this.code = CodeConstant.UN_KNOW_ERROR;
        }
    }

    public BaseException(Code code) {
        this(code.desc(), code);
    }

    public BaseException(String message, Code code) {
        super(message);
        this.code = code;
    }

    public String getMsg() {
        return getMessage();
    }

    public Code code() {
        return this.code;
    }

}
