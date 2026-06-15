package org.arthena.framework.common.exception;

import org.arthena.framework.common.constant.ErrCodeConstant;
import lombok.Getter;
import org.arthena.framework.common.exception.base.BaseRuntimeException;

/**
 * 非法参数异常
 *
 * @author zhouzhitong
 * @version 1.0
 * @since 2022/5/15 15:55
 */
@Getter
@Deprecated
public class IllegalArgumentsException extends BaseRuntimeException {

    public IllegalArgumentsException() {
        super(ErrCodeConstant.ILLEGAL_PARAMETER_ERROR);
    }

}
