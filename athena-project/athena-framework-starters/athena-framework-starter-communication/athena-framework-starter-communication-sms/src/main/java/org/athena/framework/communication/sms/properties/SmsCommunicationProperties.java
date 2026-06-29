package org.athena.framework.communication.sms.properties;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * 短信通信配置。
 *
 * @author zhouzhitong
 * @since 2026/6/29
 */
@Data
@Validated
@ConfigurationProperties(prefix = "athena.communication.sms")
public class SmsCommunicationProperties {

    private boolean enabled = false;

    @NotBlank(message = "athena.communication.sms.access-key-id 不能为空")
    private String accessKeyId;

    @NotBlank(message = "athena.communication.sms.access-key-secret 不能为空")
    private String accessKeySecret;

    @NotBlank(message = "athena.communication.sms.region-id 不能为空")
    private String regionId = "cn-hangzhou";

    @NotBlank(message = "athena.communication.sms.endpoint 不能为空")
    private String endpoint = "dysmsapi.aliyuncs.com";

    private String signName;

    private String smsUpExtendCode;

    @Min(value = 1, message = "athena.communication.sms.connect-timeout-ms 需大于 0")
    private long connectTimeoutMs = 3_000L;

    @Min(value = 1, message = "athena.communication.sms.read-timeout-ms 需大于 0")
    private long readTimeoutMs = 5_000L;
}
