package {{PACKAGE}}.security;

import org.athena.framework.security.api.spi.AuthorizationProvider;
import org.athena.framework.security.api.spi.UserContextEnricher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class {{NAME}}SecurityBeansConfiguration {

    @Bean
    AuthorizationProvider {{NAME_LOWER}}AuthorizationProvider(
            {{NAME}}AuthorizationProvider.PermissionLookup permissionLookup) {
        return new {{NAME}}AuthorizationProvider(permissionLookup);
    }

    @Bean
    UserContextEnricher {{NAME_LOWER}}UserContextEnricher(
            {{NAME}}UserContextEnricher.AttributeLookup attributeLookup) {
        return new {{NAME}}UserContextEnricher(attributeLookup);
    }
}
