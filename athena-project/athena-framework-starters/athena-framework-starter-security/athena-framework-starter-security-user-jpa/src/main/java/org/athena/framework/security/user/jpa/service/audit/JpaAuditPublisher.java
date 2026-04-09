package org.athena.framework.security.user.jpa.service.audit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.athena.framework.security.api.model.AuditRecord;
import org.athena.framework.security.api.spi.AuditPublisher;
import org.athena.framework.security.user.jpa.entity.SecAuditLogEntity;
import org.athena.framework.security.user.jpa.repository.SecAuditLogJpaRepository;

import java.time.Instant;

public class JpaAuditPublisher implements AuditPublisher {

    private final SecAuditLogJpaRepository auditLogRepository;

    private final ObjectMapper objectMapper;

    public JpaAuditPublisher(SecAuditLogJpaRepository auditLogRepository,
                             ObjectMapper objectMapper) {
        this.auditLogRepository = auditLogRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public void publish(AuditRecord record) {
        SecAuditLogEntity entity = new SecAuditLogEntity();
        entity.setCategory(record.getCategory());
        entity.setAction(record.getAction());
        entity.setResult(record.getResult());
        entity.setUserId(record.getUserId());
        entity.setUsername(record.getUsername());
        entity.setTenantId(record.getTenantId());
        entity.setResource(record.getResource());
        entity.setDetail(record.getDetail());
        entity.setRequestIp(record.getRequestIp());
        entity.setOccurredAt(record.getOccurredAt() == null ? Instant.now() : record.getOccurredAt());
        entity.setAttributesJson(toJson(record));
        auditLogRepository.save(entity);
    }

    private String toJson(AuditRecord record) {
        try {
            return objectMapper.writeValueAsString(record.getAttributes());
        } catch (JsonProcessingException ex) {
            return "{\"serializationError\":\"" + ex.getMessage() + "\"}";
        }
    }
}
