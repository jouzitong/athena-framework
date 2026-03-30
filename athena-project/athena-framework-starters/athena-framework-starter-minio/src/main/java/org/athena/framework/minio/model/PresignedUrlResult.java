package org.athena.framework.minio.model;

import lombok.Data;

import java.time.Instant;

/**
 * @author zhouzhitong
 * @since 2026/3/30
 */
@Data
public class PresignedUrlResult {

    private String url;

    private String method;

    private Instant expireAt;
}
