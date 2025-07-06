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

    private String version = "1.0.0";

}
