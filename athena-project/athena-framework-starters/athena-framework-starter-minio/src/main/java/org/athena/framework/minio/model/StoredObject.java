package org.athena.framework.minio.model;

import lombok.Data;

import java.time.Instant;

/**
 * @author zhouzhitong
 * @since 2026/3/30
 */
@Data
public class StoredObject {

    private String bucket;

    private String objectKey;

    private Long size;

    private String etag;

    private Instant lastModified;

    private String contentType;
}
