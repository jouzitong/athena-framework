package org.arthena.framework.common.exception;

import lombok.Getter;
import org.arthena.framework.common.constant.ErrCodeConstant;
import org.arthena.framework.common.utils.ErrorCodeUtils;

/**
 * @author zhouzhitong
 * @version 1.0
 * @since 2022/5/15 16:31
 */
public class BizException extends RuntimeException {

    @Getter
    private final Integer code;

    @Getter
    private final Object[] args;

    public BizException(Exception e) {
        super(e);
        if (e instanceof BizException baseException) {
            this.code = baseException.getCode();
            this.args = baseException.args;
        } else {
            this.code = ErrCodeConstant.UN_KNOW_ERROR;
            this.args = null;
        }
    }

    public BizException(Integer code, Object... args) {
        this.code = code;
        this.args = args;
    }

    public static BizException of() {
        return new BizException(ErrCodeConstant.UN_KNOW_ERROR);
    }

    public static BizException of(Integer code, Object... args) {
        return new BizException(code, args);
    }

    public static BizException illegalParam(Integer code, Object... args) {
        return new BizException(code, args);
    }

    public Integer code() {
        return this.code;
    }

    @Override
    public String getMessage() {
        return ErrorCodeUtils.getMsg(this.code, this.args);
    }

}
