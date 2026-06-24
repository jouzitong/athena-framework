package org.athena.framework.communication.config;

import org.athena.framework.communication.api.ChannelDriver;
import org.athena.framework.communication.api.CommunicationService;
import org.athena.framework.communication.service.DefaultCommunicationService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.util.List;

/**
 * 通信核心自动装配。
 *
 * @author zhouzhitong
 * @since 2026/6/24
 */
@AutoConfiguration
@EnableConfigurationProperties(CommunicationProperties.class)
public class CommunicationAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public CommunicationService communicationService(List<ChannelDriver> drivers,
                                                     CommunicationProperties properties) {
        return new DefaultCommunicationService(drivers, properties);
    }
}
