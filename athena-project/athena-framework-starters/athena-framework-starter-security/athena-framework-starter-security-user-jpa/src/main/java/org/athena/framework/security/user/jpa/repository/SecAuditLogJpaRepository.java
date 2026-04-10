package org.athena.framework.security.user.jpa.repository;

import org.athena.framework.data.jpa.repository.BaseRepository;
import org.athena.framework.security.user.jpa.entity.SecAuditLogEntity;

import java.util.List;

public interface SecAuditLogJpaRepository extends BaseRepository<SecAuditLogEntity> {

    List<SecAuditLogEntity> findTop100ByOrderByOccurredAtDescIdDesc();
}
