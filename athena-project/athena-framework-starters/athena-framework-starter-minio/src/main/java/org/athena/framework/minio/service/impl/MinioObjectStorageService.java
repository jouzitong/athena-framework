package org.athena.framework.minio.service.impl;

import io.minio.GetObjectArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;
import io.minio.errors.ErrorResponseException;
import io.minio.http.Method;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.athena.framework.minio.constant.ObjectStorageErrorCode;
import org.athena.framework.minio.exception.ObjectStorageException;
import org.athena.framework.minio.model.PresignedUrlResult;
import org.athena.framework.minio.model.PutObjectRequest;
import org.athena.framework.minio.model.StoredObject;
import org.athena.framework.minio.properties.MinioProperties;
import org.athena.framework.minio.service.ObjectStorageService;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

/**
 * @author zhouzhitong
 * @since 2026/3/30
 */
@Slf4j
public class MinioObjectStorageService implements ObjectStorageService {

    private static final long UNKNOWN_STREAM_PART_SIZE = 10L * 1024 * 1024;

    private final MinioClient minioClient;
    private final MinioProperties properties;

    public MinioObjectStorageService(MinioClient minioClient, MinioProperties properties) {
        this.minioClient = minioClient;
        this.properties = properties;
    }

    @Override
    public StoredObject putObject(PutObjectRequest request) {
        validatePutRequest(request);

        String bucket = resolveBucket(request.getBucket());
        String objectKey = request.getObjectKey();

        long objectSize = resolveObjectSize(request);
        if (properties.getMaxObjectSize() > 0 && objectSize > properties.getMaxObjectSize()) {
            throw new ObjectStorageException(ObjectStorageErrorCode.OBJECT_STORAGE_UPLOAD_ERROR,
                "object-size-over-limit", objectSize, properties.getMaxObjectSize());
        }

        try (InputStream inputStream = getInputStream(request)) {
            PutObjectArgs.Builder builder = PutObjectArgs.builder()
                .bucket(bucket)
                .object(objectKey);

            if (StringUtils.isNotBlank(request.getContentType())) {
                builder.contentType(request.getContentType());
            }
            if (request.getMetadata() != null && !request.getMetadata().isEmpty()) {
                builder.headers(request.getMetadata());
            }

            if (objectSize >= 0) {
                builder.stream(inputStream, objectSize, -1);
            } else {
                builder.stream(inputStream, -1, UNKNOWN_STREAM_PART_SIZE);
            }

            minioClient.putObject(builder.build());
            return statObject(bucket, objectKey);
        } catch (ObjectStorageException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("minio 上传对象失败, bucket={}, key={}", bucket, objectKey, e);
            throw new ObjectStorageException(ObjectStorageErrorCode.OBJECT_STORAGE_UPLOAD_ERROR, e, bucket, objectKey);
        }
    }

    @Override
    public InputStream getObject(String bucket, String objectKey) {
        String resolvedBucket = resolveBucket(bucket);
        String resolvedObjectKey = requireObjectKey(objectKey);
        try {
            return minioClient.getObject(GetObjectArgs.builder()
                .bucket(resolvedBucket)
                .object(resolvedObjectKey)
                .build());
        } catch (Exception e) {
            LOGGER.error("minio 下载对象失败, bucket={}, key={}", resolvedBucket, resolvedObjectKey, e);
            if (isNoSuchObject(e)) {
                throw new ObjectStorageException(ObjectStorageErrorCode.OBJECT_STORAGE_NOT_FOUND, e,
                    resolvedBucket, resolvedObjectKey);
            }
            throw new ObjectStorageException(ObjectStorageErrorCode.OBJECT_STORAGE_DOWNLOAD_ERROR, e,
                resolvedBucket, resolvedObjectKey);
        }
    }

    @Override
    public void removeObject(String bucket, String objectKey) {
        String resolvedBucket = resolveBucket(bucket);
        String resolvedObjectKey = requireObjectKey(objectKey);
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                .bucket(resolvedBucket)
                .object(resolvedObjectKey)
                .build());
        } catch (Exception e) {
            LOGGER.error("minio 删除对象失败, bucket={}, key={}", resolvedBucket, resolvedObjectKey, e);
            throw new ObjectStorageException(ObjectStorageErrorCode.OBJECT_STORAGE_DELETE_ERROR, e,
                resolvedBucket, resolvedObjectKey);
        }
    }

    @Override
    public StoredObject statObject(String bucket, String objectKey) {
        String resolvedBucket = resolveBucket(bucket);
        String resolvedObjectKey = requireObjectKey(objectKey);
        try {
            StatObjectResponse response = minioClient.statObject(StatObjectArgs.builder()
                .bucket(resolvedBucket)
                .object(resolvedObjectKey)
                .build());

            StoredObject storedObject = new StoredObject();
            storedObject.setBucket(resolvedBucket);
            storedObject.setObjectKey(resolvedObjectKey);
            storedObject.setEtag(response.etag());
            storedObject.setSize(response.size());
            if (response.lastModified() != null) {
                storedObject.setLastModified(response.lastModified().toInstant());
            }
            if (response.headers() != null) {
                storedObject.setContentType(response.headers().get("Content-Type"));
            }
            return storedObject;
        } catch (Exception e) {
            if (isNoSuchObject(e)) {
                throw new ObjectStorageException(ObjectStorageErrorCode.OBJECT_STORAGE_NOT_FOUND, e,
                    resolvedBucket, resolvedObjectKey);
            }
            LOGGER.error("minio 获取对象元信息失败, bucket={}, key={}", resolvedBucket, resolvedObjectKey, e);
            throw new ObjectStorageException(ObjectStorageErrorCode.OBJECT_STORAGE_DOWNLOAD_ERROR, e,
                resolvedBucket, resolvedObjectKey);
        }
    }

    @Override
    public boolean exists(String bucket, String objectKey) {
        try {
            statObject(bucket, objectKey);
            return true;
        } catch (ObjectStorageException e) {
            if (Objects.equals(e.getCode(), ObjectStorageErrorCode.OBJECT_STORAGE_NOT_FOUND)) {
                return false;
            }
            throw e;
        }
    }

    @Override
    public PresignedUrlResult getPresignedGetUrl(String bucket, String objectKey, Duration expiry) {
        return buildPresignedUrl(bucket, objectKey, Method.GET, expiry);
    }

    @Override
    public PresignedUrlResult getPresignedPutUrl(String bucket, String objectKey, Duration expiry) {
        return buildPresignedUrl(bucket, objectKey, Method.PUT, expiry);
    }

    private PresignedUrlResult buildPresignedUrl(String bucket, String objectKey, Method method, Duration expiry) {
        String resolvedBucket = resolveBucket(bucket);
        String resolvedObjectKey = requireObjectKey(objectKey);
        Duration resolvedExpiry = expiry == null ? Duration.ofSeconds(properties.getPresignExpirySeconds()) : expiry;

        int expirySeconds = (int) Math.max(1L, resolvedExpiry.getSeconds());

        try {
            String url = minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                .method(method)
                .bucket(resolvedBucket)
                .object(resolvedObjectKey)
                .expiry(expirySeconds)
                .build());

            PresignedUrlResult result = new PresignedUrlResult();
            result.setMethod(method.name());
            result.setUrl(url);
            result.setExpireAt(Instant.now().plusSeconds(expirySeconds));
            return result;
        } catch (Exception e) {
            LOGGER.error("minio 生成预签名地址失败, bucket={}, key={}, method={}",
                resolvedBucket, resolvedObjectKey, method, e);
            throw new ObjectStorageException(ObjectStorageErrorCode.OBJECT_STORAGE_PRESIGN_ERROR, e,
                resolvedBucket, resolvedObjectKey, method.name());
        }
    }

    private String resolveBucket(String bucket) {
        if (StringUtils.isNotBlank(bucket)) {
            return bucket;
        }
        if (StringUtils.isBlank(properties.getBucket())) {
            throw new ObjectStorageException(ObjectStorageErrorCode.OBJECT_STORAGE_CONFIG_ERROR, "bucket-empty");
        }
        return properties.getBucket();
    }

    private String requireObjectKey(String objectKey) {
        if (StringUtils.isBlank(objectKey)) {
            throw new ObjectStorageException(ObjectStorageErrorCode.OBJECT_STORAGE_CONFIG_ERROR, "objectKey-empty");
        }
        return objectKey;
    }

    private void validatePutRequest(PutObjectRequest request) {
        if (request == null) {
            throw new ObjectStorageException(ObjectStorageErrorCode.OBJECT_STORAGE_CONFIG_ERROR, "request-null");
        }
        requireObjectKey(request.getObjectKey());

        boolean hasBytes = request.getBytes() != null && request.getBytes().length > 0;
        boolean hasStream = request.getInputStream() != null;
        if (!hasBytes && !hasStream) {
            throw new ObjectStorageException(ObjectStorageErrorCode.OBJECT_STORAGE_CONFIG_ERROR,
                "bytes-or-stream-required");
        }
    }

    private long resolveObjectSize(PutObjectRequest request) {
        if (request.getBytes() != null && request.getBytes().length > 0) {
            return request.getBytes().length;
        }
        return request.getSize() == null ? -1L : request.getSize();
    }

    private InputStream getInputStream(PutObjectRequest request) {
        if (request.getBytes() != null && request.getBytes().length > 0) {
            return new ByteArrayInputStream(request.getBytes());
        }
        InputStream inputStream = request.getInputStream();
        if (inputStream == null) {
            throw new ObjectStorageException(ObjectStorageErrorCode.OBJECT_STORAGE_CONFIG_ERROR,
                "input-stream-null");
        }
        return inputStream;
    }

    private boolean isNoSuchObject(Exception e) {
        if (e instanceof ErrorResponseException errorResponseException
            && errorResponseException.errorResponse() != null
            && errorResponseException.errorResponse().code() != null) {
            String code = errorResponseException.errorResponse().code();
            return "NoSuchKey".equals(code) || "NoSuchObject".equals(code);
        }
        return false;
    }
}
