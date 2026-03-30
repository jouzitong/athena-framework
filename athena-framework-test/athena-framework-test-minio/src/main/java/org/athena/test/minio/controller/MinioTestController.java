package org.athena.test.minio.controller;

import org.athena.framework.minio.model.PresignedUrlResult;
import org.athena.framework.minio.model.PutObjectRequest;
import org.athena.framework.minio.model.StoredObject;
import org.athena.framework.minio.service.ObjectStorageService;
import org.athena.framework.web.vo.R;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author zhouzhitong
 * @since 2026/3/30
 */
@RestController
@RequestMapping("/files")
public class MinioTestController {

    private final ObjectStorageService objectStorageService;

    public MinioTestController(ObjectStorageService objectStorageService) {
        this.objectStorageService = objectStorageService;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public R<StoredObject> upload(@RequestParam("file") MultipartFile file,
                                  @RequestParam(value = "bucket", required = false) String bucket,
                                  @RequestParam(value = "objectKey", required = false) String objectKey) throws IOException {
        PutObjectRequest request = new PutObjectRequest();
        request.setBucket(bucket);
        request.setObjectKey(StringUtils.hasText(objectKey) ? objectKey : file.getOriginalFilename());
        request.setContentType(file.getContentType());
        request.setSize(file.getSize());
        request.setInputStream(file.getInputStream());
        return R.ok(objectStorageService.putObject(request));
    }

    @GetMapping("/{objectKey:.+}")
    public ResponseEntity<InputStreamResource> download(@PathVariable String objectKey,
                                                        @RequestParam(value = "bucket", required = false) String bucket)
        throws IOException {
        InputStream inputStream = objectStorageService.getObject(bucket, objectKey);
        InputStreamResource resource = new InputStreamResource(inputStream);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(ContentDisposition.attachment().filename(objectKey).build());
        return ResponseEntity.ok()
            .headers(headers)
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(resource);
    }

    @DeleteMapping("/{objectKey:.+}")
    public R<Void> delete(@PathVariable String objectKey,
                          @RequestParam(value = "bucket", required = false) String bucket) {
        objectStorageService.removeObject(bucket, objectKey);
        return R.ok();
    }

    @GetMapping("/{objectKey:.+}/presign-get")
    public R<PresignedUrlResult> presignGet(@PathVariable String objectKey,
                                            @RequestParam(value = "bucket", required = false) String bucket) {
        return R.ok(objectStorageService.getPresignedGetUrl(bucket, objectKey, null));
    }

    @GetMapping("/{objectKey:.+}/presign-put")
    public R<PresignedUrlResult> presignPut(@PathVariable String objectKey,
                                            @RequestParam(value = "bucket", required = false) String bucket) {
        return R.ok(objectStorageService.getPresignedPutUrl(bucket, objectKey, null));
    }

    @GetMapping("/{objectKey:.+}/stat")
    public R<StoredObject> stat(@PathVariable String objectKey,
                                @RequestParam(value = "bucket", required = false) String bucket) {
        return R.ok(objectStorageService.statObject(bucket, objectKey));
    }
}
