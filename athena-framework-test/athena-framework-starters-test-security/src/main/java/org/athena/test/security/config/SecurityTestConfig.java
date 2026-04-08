package org.athena.test.security.config;

import org.athena.framework.security.api.model.AuthorizationSnapshot;
import org.athena.framework.security.api.model.MutableUserContext;
import org.athena.framework.security.api.spi.AuthorizationProvider;
import org.athena.framework.security.api.spi.UserContextEnricher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@Configuration
public class SecurityTestConfig {

    @Bean
    public AuthorizationProvider authorizationProvider() {
        Map<String, Set<String>> userPermissions = new LinkedHashMap<>();
        userPermissions.put("U1001", Set.of("user:read", "user:create", "menu:view"));
        userPermissions.put("U1002", Set.of("user:read", "menu:view"));
        userPermissions.put("U1003", Set.of());

        return (userId, tenantId) -> userPermissions.getOrDefault(userId, Collections.emptySet());
    }

    @Bean
    public UserContextEnricher authorizationSnapshotEnricher(AuthorizationProvider authorizationProvider) {
        return new UserContextEnricher() {
            @Override
            public int order() {
                return 10;
            }

            @Override
            public void enrich(MutableUserContext context) {
                if (context.subject() == null) {
                    return;
                }
                Set<String> permissions = authorizationProvider.permissions(context.subject().userId(), context.subject().tenantId());
                context.setAuthorization(new AuthorizationSnapshot(permissions, Collections.emptySet(), Collections.emptySet()));
                context.attributes().put("permissions", permissions);
            }
        };
    }
}
