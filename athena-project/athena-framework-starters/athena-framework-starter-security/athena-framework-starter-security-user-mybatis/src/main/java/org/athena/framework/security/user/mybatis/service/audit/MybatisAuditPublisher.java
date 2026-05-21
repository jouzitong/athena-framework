package org.athena.framework.security.user.mybatis.service.audit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.athena.framework.security.api.model.AuditRecord;
import org.athena.framework.security.api.spi.AuditPublisher;
import org.athena.framework.security.user.mybatis.entity.SecAuditLogEntity;
import org.athena.framework.security.user.mybatis.repository.SecAuditLogMybatisMapper;

import java.time.Instant;

public class MybatisAuditPublisher implements AuditPublisher {

    private final SecAuditLogMybatisMapper auditLogRepository;

    private final ObjectMapper objectMapper;

    public MybatisAuditPublisher(SecAuditLogMybatisMapper auditLogRepository,
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
        auditLogRepository.insert(entity);
    }

    private String toJson(AuditRecord record) {
        try {
            return objectMapper.writeValueAsString(record.getAttributes());
        } catch (JsonProcessingException ex) {
            return "{\"serializationError\":\"" + ex.getMessage() + "\"}";
        }
    }
}
