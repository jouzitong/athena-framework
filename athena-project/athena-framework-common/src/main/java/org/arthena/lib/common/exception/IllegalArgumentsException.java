package org.arthena.lib.common.exception;

import org.arthena.lib.common.constant.CodeConstant;
import lombok.Getter;

/**
 * 非法参数异常
 *
 * @author zhouzhitong
 * @version 1.0
 * @since 2022/5/15 15:55
 */
@Getter
public class IllegalArgumentsException extends BaseException {

    public IllegalArgumentsException(String message) {
        super(message, CodeConstant.ILLEGAL_PARAMETER_ERROR);
    }

}
