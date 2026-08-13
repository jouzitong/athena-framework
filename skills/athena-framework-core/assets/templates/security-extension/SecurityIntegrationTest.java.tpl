package {{PACKAGE}}.security;

import org.athena.framework.security.api.model.MutableUserContext;
import org.athena.framework.security.api.model.Subject;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class {{NAME}}SecurityBeansConfigurationTest {

    @Test
    void usesConsumerOwnedAuthorizationAndContextData() {
        {{NAME}}AuthorizationProvider provider = new {{NAME}}AuthorizationProvider(
                (userId, tenantId) -> Set.of("{{NAME_LOWER}}:read"));
        {{NAME}}UserContextEnricher enricher = new {{NAME}}UserContextEnricher(
                (userId, tenantId) -> Map.of("dataScope", "self"));
        MutableUserContext context = new MutableUserContext();
        context.setSubject(new Subject(1L, "user", "tenant", "employee"));

        enricher.enrich(context);

        assertThat(provider.permissions(1L, "tenant")).containsExactly("{{NAME_LOWER}}:read");
        assertThat(context.getAttributes()).containsEntry("dataScope", "self");
    }
}
