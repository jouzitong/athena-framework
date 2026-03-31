package org.athena.test.minio.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * MinIO 调试页面入口。
 *
 * @author zhouzhitong
 * @since 2026/3/30
 */
@Controller
public class MinioPageController {

    @GetMapping("/minio-test")
    public String minioTestPage() {
        return "forward:/minio-test.html";
    }
}
