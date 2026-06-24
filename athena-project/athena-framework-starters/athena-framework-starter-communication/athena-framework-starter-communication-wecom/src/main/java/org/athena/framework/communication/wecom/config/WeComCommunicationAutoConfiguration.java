package org.athena.framework.communication.wecom.config;

import org.athena.framework.communication.api.ChannelDriver;
import org.athena.framework.communication.wecom.properties.WeComCommunicationProperties;
import org.athena.framework.communication.wecom.service.WeComChannelDriver;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.net.http.HttpClient;
import java.time.Duration;

/**
 * 企业微信通信自动装配。
 *
 * @author zhouzhitong
 * @since 2026/6/24
 */
@AutoConfiguration
@ConditionalOnProperty(prefix = "athena.communication.wecom", name = "enabled", havingValue = "true")
@EnableConfigurationProperties(WeComCommunicationProperties.class)
public class WeComCommunicationAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(name = "weComHttpClient")
    public HttpClient weComHttpClient(WeComCommunicationProperties properties) {
        return HttpClient.newBuilder()
            .connectTimeout(Duration.ofMillis(properties.getConnectTimeoutMs()))
            .build();
    }

    @Bean
    @ConditionalOnMissingBean(name = "weComChannelDriver")
    public ChannelDriver weComChannelDriver(HttpClient weComHttpClient,
                                            WeComCommunicationProperties properties) {
        return new WeComChannelDriver(weComHttpClient, properties);
    }
}
