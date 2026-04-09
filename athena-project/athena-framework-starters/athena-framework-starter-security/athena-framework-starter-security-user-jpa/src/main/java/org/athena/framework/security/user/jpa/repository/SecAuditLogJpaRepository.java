package org.athena.framework.security.user.jpa.repository;

import org.athena.framework.security.user.jpa.entity.SecAuditLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SecAuditLogJpaRepository extends JpaRepository<SecAuditLogEntity, Long> {

    List<SecAuditLogEntity> findTop100ByOrderByOccurredAtDescIdDesc();
}
