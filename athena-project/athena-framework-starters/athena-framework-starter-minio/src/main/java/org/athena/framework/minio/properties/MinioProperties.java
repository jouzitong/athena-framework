package org.athena.framework.minio.properties;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * @author zhouzhitong
 * @since 2026/3/30
 */
@Data
@Validated
@ConfigurationProperties(prefix = "athena.minio")
public class MinioProperties {

    private boolean enabled = false;

    @NotBlank(message = "athena.minio.endpoint 不能为空")
    private String endpoint;

    @NotBlank(message = "athena.minio.access-key 不能为空")
    private String accessKey;

    @NotBlank(message = "athena.minio.secret-key 不能为空")
    private String secretKey;

    private String region;

    @NotBlank(message = "athena.minio.bucket 不能为空")
    private String bucket;

    /**
     * null 表示由 endpoint 自行推断。
     */
    private Boolean secure;

    private boolean autoCreateBucket = true;

    @Min(value = 1, message = "connectTimeoutMs 需大于 0")
    private long connectTimeoutMs = 3_000L;

    @Min(value = 1, message = "writeTimeoutMs 需大于 0")
    private long writeTimeoutMs = 10_000L;

    @Min(value = 1, message = "readTimeoutMs 需大于 0")
    private long readTimeoutMs = 10_000L;

    @Min(value = 1, message = "presignExpirySeconds 需大于 0")
    private int presignExpirySeconds = 900;

    /**
     * 0 表示不限制。
     */
    private long maxObjectSize = 0L;
}
