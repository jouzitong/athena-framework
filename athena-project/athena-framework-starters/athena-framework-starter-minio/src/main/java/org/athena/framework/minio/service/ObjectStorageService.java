package org.athena.framework.minio.service;

import org.athena.framework.minio.model.PresignedUrlResult;
import org.athena.framework.minio.model.PutObjectRequest;
import org.athena.framework.minio.model.StoredObject;

import java.io.InputStream;
import java.time.Duration;

/**
 * 对象存储统一服务接口。
 * <p>
 * 设计目标：
 * <li>对业务层屏蔽 MinIO SDK 细节，避免业务代码与具体存储实现强耦合</li>
 * <li>保持方法语义稳定，便于后续扩展为其他对象存储实现（如 S3/OSS）</li>
 * <li>将异常统一转换为框架异常体系，便于全局统一处理</li>
 *
 * 使用约定：
 * <li>方法中的 {@code bucket} 允许为空；为空时由实现使用默认配置 bucket</li>
 * <li>{@code objectKey} 必须是业务唯一对象键，通常建议按目录前缀进行规范化命名</li>
 * <li>具体失败信息由实现转换为统一错误码，不建议业务直接依赖底层 SDK 异常</li>
 *
 * @author zhouzhitong
 * @since 2026/3/30
 */
public interface ObjectStorageService {

    /**
     * 上传对象。
     *
     * @param request 上传请求，包含 bucket、objectKey、内容流/字节数组、内容类型等信息
     * @return 上传后的对象元信息（如 etag、size、lastModified）
     */
    StoredObject putObject(PutObjectRequest request);

    /**
     * 下载对象流。
     *
     * @param bucket    bucket 名称，可为空；为空时使用默认 bucket
     * @param objectKey 对象键，不能为空
     * @return 对象输入流；调用方负责在使用完成后关闭流
     */
    InputStream getObject(String bucket, String objectKey);

    /**
     * 删除对象。
     *
     * @param bucket    bucket 名称，可为空；为空时使用默认 bucket
     * @param objectKey 对象键，不能为空
     */
    void removeObject(String bucket, String objectKey);

    /**
     * 查询对象元信息。
     *
     * @param bucket    bucket 名称，可为空；为空时使用默认 bucket
     * @param objectKey 对象键，不能为空
     * @return 对象元信息（size、etag、lastModified、contentType 等）
     */
    StoredObject statObject(String bucket, String objectKey);

    /**
     * 判断对象是否存在。
     *
     * @param bucket    bucket 名称，可为空；为空时使用默认 bucket
     * @param objectKey 对象键，不能为空
     * @return true 表示对象存在，false 表示对象不存在
     */
    boolean exists(String bucket, String objectKey);

    /**
     * 生成下载预签名地址（GET）。
     *
     * @param bucket    bucket 名称，可为空；为空时使用默认 bucket
     * @param objectKey 对象键，不能为空
     * @param expiry    过期时长；为空时由实现使用默认配置时长
     * @return 预签名结果，包含 URL、方法、过期时间
     */
    PresignedUrlResult getPresignedGetUrl(String bucket, String objectKey, Duration expiry);

    /**
     * 生成上传预签名地址（PUT）。
     *
     * @param bucket    bucket 名称，可为空；为空时使用默认 bucket
     * @param objectKey 对象键，不能为空
     * @param expiry    过期时长；为空时由实现使用默认配置时长
     * @return 预签名结果，包含 URL、方法、过期时间
     */
    PresignedUrlResult getPresignedPutUrl(String bucket, String objectKey, Duration expiry);
}
