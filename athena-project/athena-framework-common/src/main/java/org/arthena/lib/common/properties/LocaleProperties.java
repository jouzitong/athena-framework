package org.arthena.lib.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 多语言配置
 *
 * @author zhouzhitong
 * @since 2023/5/29
 */
@Component
@ConfigurationProperties(prefix = "lib.locale")
@Data
public class LocaleProperties {

    /**
     * 语言环境 (默认中文)
     */
    private String locale = "zh_CN";

    /**
     * 是否启用多语言 (默认关闭)
     */
    private boolean enableLocale = false;

}
