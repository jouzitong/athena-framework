package org.athena.framework.security.user.jpa.service.audit;

import org.athena.framework.security.api.event.AuthenticationFailureEvent;
import org.athena.framework.security.api.event.AuthenticationSuccessEvent;
import org.athena.framework.security.api.event.AuthorizationDecisionEvent;
import org.athena.framework.security.api.model.AuditRecord;
import org.athena.framework.security.api.model.UserContext;
import org.athena.framework.security.api.spi.AuditPublisher;
import org.springframework.context.event.EventListener;

import java.time.Instant;
import java.util.Arrays;

public class SecurityAuditEventListener {

    private final AuditPublisher auditPublisher;

    public SecurityAuditEventListener(AuditPublisher auditPublisher) {
        this.auditPublisher = auditPublisher;
    }

    @EventListener
    public void onAuthenticationSuccess(AuthenticationSuccessEvent event) {
        UserContext context = event.context();
        AuditRecord record = new AuditRecord();
        record.setCategory("AUTHN");
        record.setAction("LOGIN");
        record.setResult("SUCCESS");
        fillContext(record, context);
        record.setResource("/auth/login");
        record.setDetail("authentication success");
        auditPublisher.publish(record);
    }

    @EventListener
    public void onAuthenticationFailure(AuthenticationFailureEvent event) {
        AuditRecord record = new AuditRecord();
        record.setCategory("AUTHN");
        record.setAction("LOGIN");
        record.setResult("FAIL");
        record.setUsername(event.username());
        record.setResource("/auth/login");
        record.setDetail(event.code() + ": " + event.message());
        record.setOccurredAt(Instant.now());
        record.getAttributes().put("code", event.code());
        auditPublisher.publish(record);
    }

    @EventListener
    public void onAuthorizationDecision(AuthorizationDecisionEvent event) {
        AuditRecord record = new AuditRecord();
        record.setCategory("AUTHZ");
        record.setAction("PERMISSION_CHECK");
        record.setResult(event.granted() ? "SUCCESS" : "FAIL");
        fillContext(record, event.context());
        record.setResource(event.resource());
        record.setDetail(event.message());
        record.getAttributes().put("permissions", Arrays.asList(event.permissions()));
        record.getAttributes().put("requireAll", event.requireAll());
        auditPublisher.publish(record);
    }

    private void fillContext(AuditRecord record, UserContext context) {
        if (context != null && context.subject() != null) {
            record.setUserId(context.subject().userId());
            record.setUsername(context.subject().username());
            record.setTenantId(context.subject().tenantId());
        }
        if (context != null && context.authn() != null) {
            record.setOccurredAt(context.authn().authenticatedAt());
            record.getAttributes().put("authType", context.authn().authType());
        } else {
            record.setOccurredAt(Instant.now());
        }
        if (context != null && context.authorization() != null) {
            record.getAttributes().put("roles", context.authorization().roles());
            record.getAttributes().put("permissions", context.authorization().permissions());
        }
    }
}
