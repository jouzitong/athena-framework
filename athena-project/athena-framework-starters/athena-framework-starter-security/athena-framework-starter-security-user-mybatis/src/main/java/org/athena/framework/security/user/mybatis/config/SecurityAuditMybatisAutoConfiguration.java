package org.athena.framework.security.user.mybatis.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.session.SqlSessionFactory;
import org.athena.framework.security.api.spi.AuditPublisher;
import org.athena.framework.security.starter.marker.SecurityCoreMarker;
import org.athena.framework.security.user.mybatis.repository.SecAuditLogMybatisMapper;
import org.athena.framework.security.user.mybatis.service.audit.MybatisAuditPublisher;
import org.athena.framework.security.user.mybatis.service.audit.SecurityAuditEventListener;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@AutoConfigureAfter(SecurityUserMybatisAutoConfiguration.class)
@ConditionalOnClass(SqlSessionFactory.class)
@ConditionalOnBean(SecurityCoreMarker.class)
@ConditionalOnProperty(prefix = "athena.security.audit", name = "enabled", havingValue = "true")
public class SecurityAuditMybatisAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(AuditPublisher.class)
    public AuditPublisher auditPublisher(SecAuditLogMybatisMapper auditLogMapper,
                                         ObjectMapper objectMapper) {
        return new MybatisAuditPublisher(auditLogMapper, objectMapper);
    }

    @Bean
    @ConditionalOnMissingBean
    public SecurityAuditEventListener securityAuditEventListener(AuditPublisher auditPublisher) {
        return new SecurityAuditEventListener(auditPublisher);
    }
}
