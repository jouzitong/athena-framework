package org.athena.framework.communication.email.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 邮件通信配置。
 *
 * @author zhouzhitong
 * @since 2026/6/24
 */
@Data
@ConfigurationProperties(prefix = "athena.communication.email")
public class EmailCommunicationProperties {

    private boolean enabled = false;

    private String from;

    private boolean html = false;
}
