package org.athena.framework.communication.sms.config;

import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.teaopenapi.models.Config;
import org.athena.framework.communication.api.ChannelDriver;
import org.athena.framework.communication.sms.properties.SmsCommunicationProperties;
import org.athena.framework.communication.sms.service.SmsChannelDriver;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * 短信通信自动装配。
 *
 * @author zhouzhitong
 * @since 2026/6/29
 */
@AutoConfiguration
@ConditionalOnClass(Client.class)
@ConditionalOnProperty(prefix = "athena.communication.sms", name = "enabled", havingValue = "true")
@EnableConfigurationProperties(SmsCommunicationProperties.class)
public class SmsCommunicationAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(name = "aliyunSmsClient")
    public Client aliyunSmsClient(SmsCommunicationProperties properties) throws Exception {
        Config config = new Config()
            .setAccessKeyId(properties.getAccessKeyId())
            .setAccessKeySecret(properties.getAccessKeySecret())
            .setRegionId(properties.getRegionId())
            .setEndpoint(properties.getEndpoint())
            .setConnectTimeout(Math.toIntExact(properties.getConnectTimeoutMs()))
            .setReadTimeout(Math.toIntExact(properties.getReadTimeoutMs()));
        return new Client(config);
    }

    @Bean
    @ConditionalOnMissingBean(name = "smsChannelDriver")
    public ChannelDriver smsChannelDriver(Client aliyunSmsClient,
                                          SmsCommunicationProperties properties) {
        return new SmsChannelDriver(aliyunSmsClient, properties);
    }
}
