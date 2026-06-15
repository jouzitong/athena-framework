package org.athena.framework.minio.constant;

import org.arthena.framework.common.constant.ErrCodeConstant;

/**
 * 对象存储错误码别名，保持语义明确。
 *
 * @author zhouzhitong
 * @since 2026/3/30
 */
public interface ObjectStorageErrorCode {

    Integer OBJECT_STORAGE_UPLOAD_ERROR = ErrCodeConstant.OBJECT_STORAGE_UPLOAD_ERROR;

    Integer OBJECT_STORAGE_DOWNLOAD_ERROR = ErrCodeConstant.OBJECT_STORAGE_DOWNLOAD_ERROR;

    Integer OBJECT_STORAGE_DELETE_ERROR = ErrCodeConstant.OBJECT_STORAGE_DELETE_ERROR;

    Integer OBJECT_STORAGE_NOT_FOUND = ErrCodeConstant.OBJECT_STORAGE_NOT_FOUND;

    Integer OBJECT_STORAGE_PRESIGN_ERROR = ErrCodeConstant.OBJECT_STORAGE_PRESIGN_ERROR;

    Integer OBJECT_STORAGE_BUCKET_INIT_ERROR = ErrCodeConstant.OBJECT_STORAGE_BUCKET_INIT_ERROR;

    Integer OBJECT_STORAGE_CONFIG_ERROR = ErrCodeConstant.OBJECT_STORAGE_CONFIG_ERROR;
}
