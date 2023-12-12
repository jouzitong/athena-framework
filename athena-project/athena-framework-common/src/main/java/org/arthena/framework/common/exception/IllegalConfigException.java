package org.arthena.framework.common.exception;

import org.arthena.framework.common.constant.CodeConstant;
import lombok.Getter;

/**
 * 非法配置异常
 *
 * @author zhouzhitong
 * @version 1.0
 * @since 2022/5/15 15:55
 */
@Getter
public class IllegalConfigException extends BaseException {

    public IllegalConfigException(String message) {
        super(message, CodeConstant.CONFIG_ERROR);
    }

}
