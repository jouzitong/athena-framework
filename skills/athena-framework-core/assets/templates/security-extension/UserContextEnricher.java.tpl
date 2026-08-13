package {{PACKAGE}}.security;

import org.athena.framework.security.api.model.MutableUserContext;
import org.athena.framework.security.api.spi.UserContextEnricher;

import java.util.Map;

public final class {{NAME}}UserContextEnricher implements UserContextEnricher {

    private final AttributeLookup attributeLookup;

    public {{NAME}}UserContextEnricher(AttributeLookup attributeLookup) {
        this.attributeLookup = attributeLookup;
    }

    @Override
    public int order() {
        return 100;
    }

    @Override
    public void enrich(MutableUserContext context) {
        if (context == null || context.subject() == null) {
            return;
        }
        Map<String, Object> attributes = attributeLookup.findAttributes(
                context.subject().userId(), context.subject().tenantId());
        if (attributes != null) {
            context.getAttributes().putAll(attributes);
        }
    }

    @FunctionalInterface
    public interface AttributeLookup {
        Map<String, Object> findAttributes(Long userId, String tenantId);
    }
}
