package org.athena.framework.web.properties;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 *
 * @author zhouzhitong
 * @since 2026/1/31
 */
@Component
@ConfigurationProperties("lib.web")
@Data
@NoArgsConstructor
public class LibWebProperties {

    /**
     * 需要扫描的枚举包
     */
    private List<String> enumPackages;

    /**
     * 响应签名配置
     */
    private Sign sign = new Sign();

    @Data
    @NoArgsConstructor
    public static class Sign {
        /**
         * 是否开启响应签名
         */
        private boolean enabled = false;

        /**
         * HMAC 签名密钥
         */
        private String secret;

        /**
         * 密钥标识（用于轮换）
         */
        private String keyId = "default";
    }

}
