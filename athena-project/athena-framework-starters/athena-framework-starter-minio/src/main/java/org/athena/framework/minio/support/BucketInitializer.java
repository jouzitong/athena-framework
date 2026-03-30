package org.athena.framework.minio.support;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.athena.framework.minio.constant.ObjectStorageErrorCode;
import org.athena.framework.minio.exception.ObjectStorageException;
import org.athena.framework.minio.properties.MinioProperties;

/**
 * @author zhouzhitong
 * @since 2026/3/30
 */
@Slf4j
public class BucketInitializer {

    private final MinioClient minioClient;
    private final MinioProperties properties;

    public BucketInitializer(MinioClient minioClient, MinioProperties properties) {
        this.minioClient = minioClient;
        this.properties = properties;
    }

    public void init() {
        String bucket = properties.getBucket();
        if (StringUtils.isBlank(bucket)) {
            throw new ObjectStorageException(ObjectStorageErrorCode.OBJECT_STORAGE_CONFIG_ERROR, "bucket-empty");
        }

        try {
            boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
            if (exists) {
                LOGGER.info("minio bucket 已存在, bucket={}", bucket);
                return;
            }

            if (!properties.isAutoCreateBucket()) {
                LOGGER.warn("minio bucket 不存在且已关闭自动创建, bucket={}", bucket);
                return;
            }

            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
            LOGGER.info("minio bucket 创建成功, bucket={}", bucket);
        } catch (Exception e) {
            LOGGER.error("minio bucket 初始化失败, bucket={}", bucket, e);
            throw new ObjectStorageException(ObjectStorageErrorCode.OBJECT_STORAGE_BUCKET_INIT_ERROR, e, bucket);
        }
    }
}
