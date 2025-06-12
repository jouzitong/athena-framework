package org.arthena.framework.common.exception.base;

import lombok.Getter;
import org.arthena.framework.common.constant.CodeConstant;
import org.arthena.framework.common.utils.ErrorCodeUtils;

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

    public BaseException(Integer code,Object... args) {
        super(ErrorCodeUtils.getMsg(code, args));
        this.code = code;
        this.args = args;
    }

    public Integer code() {
        return this.code;
    }

    @Override
    public String getMessage() {
        return ErrorCodeUtils.getMsg(this.code, this.args);
    }

}
