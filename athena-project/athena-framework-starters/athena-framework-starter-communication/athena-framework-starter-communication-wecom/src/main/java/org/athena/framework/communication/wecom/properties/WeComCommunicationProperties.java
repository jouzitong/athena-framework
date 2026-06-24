package org.athena.framework.communication.wecom.properties;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * 企业微信通信配置。
 *
 * @author zhouzhitong
 * @since 2026/6/24
 */
@Data
@Validated
@ConfigurationProperties(prefix = "athena.communication.wecom")
public class WeComCommunicationProperties {

    private boolean enabled = false;

    @NotBlank(message = "athena.communication.wecom.corp-id 不能为空")
    private String corpId;

    @NotBlank(message = "athena.communication.wecom.corp-secret 不能为空")
    private String corpSecret;

    @Min(value = 1, message = "athena.communication.wecom.agent-id 需大于 0")
    private long agentId;

    private String baseUrl = "https://qyapi.weixin.qq.com";

    @Min(value = 1, message = "athena.communication.wecom.connect-timeout-ms 需大于 0")
    private long connectTimeoutMs = 3_000L;
}
