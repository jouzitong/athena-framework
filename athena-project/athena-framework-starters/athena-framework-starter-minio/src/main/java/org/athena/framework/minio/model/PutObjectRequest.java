package org.athena.framework.minio.model;

import lombok.Data;

import java.io.InputStream;
import java.util.Map;

/**
 * @author zhouzhitong
 * @since 2026/3/30
 */
@Data
public class PutObjectRequest {

    /**
     * 允许不传，默认使用配置中的 bucket
     */
    private String bucket;

    private String objectKey;

    private String contentType;

    /**
     * 输入流大小，未知可传 -1
     */
    private Long size = -1L;

    /**
     * 与 bytes 二选一
     */
    private InputStream inputStream;

    /**
     * 与 inputStream 二选一
     */
    private byte[] bytes;

    private Map<String, String> metadata;
}
