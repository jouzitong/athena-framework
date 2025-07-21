package org.arthena.framework.common.exception;

import org.arthena.framework.common.constant.CodeConstant;
import lombok.Getter;
import org.arthena.framework.common.exception.base.BaseRuntimeException;

/**
 * 功能未开发异常
 *
 * @author zhouzhitong
 * @version 1.0
 * @since 2022/5/15 15:55
 */
@Getter
public class TodoException extends BaseRuntimeException {

    public TodoException(int code) {
        super(code);
    }

    public TodoException() {
        super(CodeConstant.TODO_ERROR);
    }
}
