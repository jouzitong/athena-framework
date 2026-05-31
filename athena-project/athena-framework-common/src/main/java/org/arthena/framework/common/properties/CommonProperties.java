package org.arthena.framework.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author zhouzhitong
 * @since 2025/7/6
 **/
@Component
@Data
@ConfigurationProperties(prefix = "lib.common")
public class CommonProperties {

    /**
     * 公共库版本号（用于透出配置版本信息）。
     */
    private String version = "1.0.0";

    /**
     * 错误码外部配置目录。
     *
     * <p>默认值为 {@code config}，表示从应用启动目录读取
     * {@code ./config/ErrorCode-{locale}.properties}。
     *
     * <p>例如部署结构：
     * <pre>
     * app.jar
     * config/ErrorCode-zh.properties
     * config/ErrorCode-en.properties
     * </pre>
     *
     * <p>可通过配置项覆盖：
     * <pre>
     * lib.common.err-code-path=/data/app/config
     * </pre>
     */
    private String errCodePath = "config";
}
