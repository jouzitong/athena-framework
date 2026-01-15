package org.arthena.framework.common.exception;

import lombok.Getter;
import org.arthena.framework.common.constant.CodeConstant;
import org.arthena.framework.common.exception.base.BaseRuntimeException;

/**
 * 功能未开发异常
 *
 * @author zhouzhitong
 * @version 1.0
 * @since 2022/5/15 15:55
 */
@Getter
public class NotSupportException extends BaseRuntimeException {

    public NotSupportException(int code) {
        super(code);
    }

    public NotSupportException() {
        super(CodeConstant.NOT_SUPPORT_ERROR);
    }
}
