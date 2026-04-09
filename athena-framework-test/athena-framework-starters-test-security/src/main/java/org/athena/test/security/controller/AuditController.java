package org.athena.test.security.controller;

import org.athena.framework.security.user.jpa.repository.SecAuditLogJpaRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/audit")
public class AuditController {

    private final SecAuditLogJpaRepository auditLogRepository;

    public AuditController(SecAuditLogJpaRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @GetMapping("/logs")
    public Object logs() {
        return auditLogRepository.findTop100ByOrderByOccurredAtDescIdDesc();
    }
}
