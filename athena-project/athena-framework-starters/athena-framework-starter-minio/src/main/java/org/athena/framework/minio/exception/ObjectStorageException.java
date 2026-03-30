package org.athena.framework.minio.exception;

import org.arthena.framework.common.exception.base.BaseRuntimeException;

/**
 * @author zhouzhitong
 * @since 2026/3/30
 */
public class ObjectStorageException extends BaseRuntimeException {

    public ObjectStorageException(Integer code, Object... args) {
        super(code, args);
    }

    public ObjectStorageException(Integer code, Throwable cause, Object... args) {
        super(code, args);
        initCause(cause);
    }
}
