package org.athena.framework.communication.email.config;

import org.athena.framework.communication.api.ChannelDriver;
import org.athena.framework.communication.email.properties.EmailCommunicationProperties;
import org.athena.framework.communication.email.service.EmailChannelDriver;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;

/**
 * 邮件通信自动装配。
 *
 * @author zhouzhitong
 * @since 2026/6/24
 */
@AutoConfiguration
@ConditionalOnClass(JavaMailSender.class)
@ConditionalOnProperty(prefix = "athena.communication.email", name = "enabled", havingValue = "true")
@EnableConfigurationProperties(EmailCommunicationProperties.class)
public class EmailCommunicationAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(name = "emailChannelDriver")
    public ChannelDriver emailChannelDriver(JavaMailSender mailSender,
                                            EmailCommunicationProperties properties) {
        return new EmailChannelDriver(mailSender, properties);
    }
}
