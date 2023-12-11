package org.authena.framework.web.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author zhouzhitong
 * @since 2022/12/15
 */
@Data
@Component
@ConfigurationProperties(prefix = "lib.enum")
public class GlobalEnumProperties {

    private List<String> basePackages;

}
