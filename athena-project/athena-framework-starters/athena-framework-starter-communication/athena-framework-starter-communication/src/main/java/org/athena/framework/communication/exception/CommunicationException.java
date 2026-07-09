package org.athena.framework.communication.exception;

import org.arthena.framework.common.exception.BizException;

/**
 * 通信模块统一异常。
 *
 * @author zhouzhitong
 * @since 2026/6/24
 */
public class CommunicationException extends BizException {

    public CommunicationException(Integer code, Object... args) {
        super(code, args);
    }

    public CommunicationException(Integer code, Throwable cause, Object... args) {
        super(code, cause, args);
    }
}
