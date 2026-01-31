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

}
