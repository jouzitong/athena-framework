package org.athena.framework.security.user.jpa.config;

import jakarta.persistence.EntityManager;
import org.athena.framework.security.api.spi.MenuProvider;
import org.athena.framework.security.starter.marker.SecurityCoreMarker;
import org.athena.framework.security.user.jpa.repository.SecMenuJpaRepository;
import org.athena.framework.security.user.jpa.service.menu.JpaMenuProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@AutoConfigureAfter(SecurityRbacJpaAutoConfiguration.class)
@ConditionalOnClass(EntityManager.class)
@ConditionalOnBean(SecurityCoreMarker.class)
@ConditionalOnProperty(prefix = "athena.security.menu", name = "enabled", havingValue = "true")
public class SecurityMenuJpaAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(MenuProvider.class)
    public MenuProvider menuProvider(SecMenuJpaRepository menuRepository) {
        return new JpaMenuProvider(menuRepository);
    }
}
