package org.athena.framework.security.user.jpa.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.athena.framework.security.api.spi.AuditPublisher;
import org.athena.framework.security.starter.marker.SecurityCoreMarker;
import org.athena.framework.security.user.jpa.repository.SecAuditLogJpaRepository;
import org.athena.framework.security.user.jpa.service.audit.JpaAuditPublisher;
import org.athena.framework.security.user.jpa.service.audit.SecurityAuditEventListener;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@AutoConfigureAfter(SecurityUserJpaAutoConfiguration.class)
@ConditionalOnClass(EntityManager.class)
@ConditionalOnBean(SecurityCoreMarker.class)
@ConditionalOnProperty(prefix = "athena.security.audit", name = "enabled", havingValue = "true")
public class SecurityAuditJpaAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(AuditPublisher.class)
    public AuditPublisher auditPublisher(SecAuditLogJpaRepository auditLogRepository,
                                         ObjectMapper objectMapper) {
        return new JpaAuditPublisher(auditLogRepository, objectMapper);
    }

    @Bean
    @ConditionalOnMissingBean
    public SecurityAuditEventListener securityAuditEventListener(AuditPublisher auditPublisher) {
        return new SecurityAuditEventListener(auditPublisher);
    }
}
