package org.arthena.framework.common.exception;

import org.arthena.framework.common.constant.CodeConstant;
import lombok.Getter;

/**
 * 非法参数异常
 *
 * @author zhouzhitong
 * @version 1.0
 * @since 2022/5/15 15:55
 */
@Getter
public class IllegalParameterMethodException extends BaseException {

    private final Object parameter;

    public IllegalParameterMethodException(Object parameter) {
        super(parameter.toString(), CodeConstant.ILLEGAL_PARAMETER_ERROR);
        this.parameter = parameter;
    }

    @Override
    public String getMsg() {
        return "非法参数异常: " + parameter;
    }
}
