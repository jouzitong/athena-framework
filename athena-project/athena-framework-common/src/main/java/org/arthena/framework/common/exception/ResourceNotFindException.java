package org.arthena.framework.common.exception;

import org.arthena.framework.common.constant.CodeConstant;
import org.arthena.framework.common.exception.base.BaseRuntimeException;

/**
 * 资源不存在异常
 *
 * @author zhouzhitong
 * @version 1.0
 * @since 2022/5/15 15:55
 */
public class ResourceNotFindException extends BaseRuntimeException {

    public ResourceNotFindException() {
        super(CodeConstant.RESOURCE_NOT_FOUND);
    }

}
