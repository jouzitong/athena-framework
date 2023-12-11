package org.arthena.lib.common.exception;

import org.arthena.lib.common.base.BaseEnum;
import org.arthena.lib.common.constant.CodeConstant;

/**
 * 枚举值异常
 *
 * @author zhouzhitong
 * @since 2023/2/19
 */
public class EnumNotFindException extends BaseException {

    public EnumNotFindException(String message) {
        super(message, CodeConstant.ILLEGAL_PARAMETER_ERROR);
    }

    public static EnumNotFindException instant(Class<? extends BaseEnum> e, int code) {
        String msg = e + " 枚举值异常, 找不到code = " + code;
        return new EnumNotFindException(msg);
    }

}
