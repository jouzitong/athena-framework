package org.arthena.framework.common.exception;

import org.arthena.framework.common.base.BaseEnum;
import org.arthena.framework.common.constant.CodeConstant;
import org.arthena.framework.common.exception.base.BaseRuntimeException;

/**
 * 枚举值异常
 *
 * @author zhouzhitong
 * @since 2023/2/19
 */
public class EnumNotFindException extends BaseRuntimeException {

    public EnumNotFindException() {
        super(CodeConstant.ILLEGAL_PARAMETER_ERROR);
    }

    public static EnumNotFindException instant(Class<? extends BaseEnum> e, int code) {
        String msg = e + " 枚举值异常, 找不到code = " + code;
        return new EnumNotFindException();
    }

}
