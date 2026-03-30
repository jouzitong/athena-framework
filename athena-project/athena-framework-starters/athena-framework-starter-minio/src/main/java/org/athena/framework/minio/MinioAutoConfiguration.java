package org.athena.framework.minio;

import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.apache.commons.lang3.StringUtils;
import org.athena.framework.minio.constant.ObjectStorageErrorCode;
import org.athena.framework.minio.exception.ObjectStorageException;
import org.athena.framework.minio.properties.MinioProperties;
import org.athena.framework.minio.service.ObjectStorageService;
import org.athena.framework.minio.service.impl.MinioObjectStorageService;
import org.athena.framework.minio.support.BucketInitializer;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.time.Duration;
import java.util.Locale;

/**
 * @author zhouzhitong
 * @since 2026/3/30
 */
@AutoConfiguration
@ConditionalOnClass(MinioClient.class)
@ConditionalOnProperty(prefix = "athena.minio", name = "enabled", havingValue = "true")
@EnableConfigurationProperties(MinioProperties.class)
@Slf4j
public class MinioAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public MinioClient minioClient(MinioProperties properties) {
        validate(properties);

        OkHttpClient httpClient = new OkHttpClient.Builder()
            .connectTimeout(Duration.ofMillis(properties.getConnectTimeoutMs()))
            .writeTimeout(Duration.ofMillis(properties.getWriteTimeoutMs()))
            .readTimeout(Duration.ofMillis(properties.getReadTimeoutMs()))
            .build();

        String endpoint = normalizeEndpoint(properties.getEndpoint(), properties.getSecure());
        MinioClient.Builder builder = MinioClient.builder()
            .endpoint(endpoint)
            .credentials(properties.getAccessKey(), properties.getSecretKey())
            .httpClient(httpClient);

        if (StringUtils.isNotBlank(properties.getRegion())) {
            builder.region(properties.getRegion());
        }

        LOGGER.info("minio 自动配置开始加载, endpoint={}, bucket={}, autoCreateBucket={}",
            endpoint, properties.getBucket(), properties.isAutoCreateBucket());
        return builder.build();
    }

    @Bean
    @ConditionalOnMissingBean
    public BucketInitializer bucketInitializer(MinioClient minioClient, MinioProperties properties) {
        BucketInitializer initializer = new BucketInitializer(minioClient, properties);
        initializer.init();
        return initializer;
    }

    @Bean
    @ConditionalOnMissingBean
    public ObjectStorageService objectStorageService(MinioClient minioClient, MinioProperties properties) {
        return new MinioObjectStorageService(minioClient, properties);
    }

    private void validate(MinioProperties properties) {
        if (StringUtils.isBlank(properties.getEndpoint())) {
            throw new ObjectStorageException(ObjectStorageErrorCode.OBJECT_STORAGE_CONFIG_ERROR, "endpoint-empty");
        }
        if (StringUtils.isBlank(properties.getAccessKey())) {
            throw new ObjectStorageException(ObjectStorageErrorCode.OBJECT_STORAGE_CONFIG_ERROR, "accessKey-empty");
        }
        if (StringUtils.isBlank(properties.getSecretKey())) {
            throw new ObjectStorageException(ObjectStorageErrorCode.OBJECT_STORAGE_CONFIG_ERROR, "secretKey-empty");
        }
        if (StringUtils.isBlank(properties.getBucket())) {
            throw new ObjectStorageException(ObjectStorageErrorCode.OBJECT_STORAGE_CONFIG_ERROR, "bucket-empty");
        }
    }

    private String normalizeEndpoint(String endpoint, Boolean secure) {
        if (secure == null) {
            return endpoint;
        }

        String lower = endpoint.toLowerCase(Locale.ROOT);
        if (lower.startsWith("http://")) {
            return secure ? "https://" + endpoint.substring("http://".length()) : endpoint;
        }
        if (lower.startsWith("https://")) {
            return secure ? endpoint : "http://" + endpoint.substring("https://".length());
        }
        return (secure ? "https://" : "http://") + endpoint;
    }
}
