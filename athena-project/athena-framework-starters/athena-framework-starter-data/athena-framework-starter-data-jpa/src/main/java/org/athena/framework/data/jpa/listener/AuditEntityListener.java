package org.athena.framework.data.jpa.listener;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.extern.slf4j.Slf4j;
import org.athena.framework.data.jpa.domain.base.AuditableEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
public class AuditEntityListener {

    @PrePersist
    public void preInsert(AuditableEntity entity) {
        entity.setCreatedAt(LocalDateTime.now());
        entity.setCreatedBy("99999");
//        entity.setCreatedBy(SecurityContext.currentUser());
    }

    @PreUpdate
    public void preUpdate(AuditableEntity entity) {
        entity.setUpdatedAt(LocalDateTime.now());
        entity.setUpdatedBy("99999");
//        entity.setUpdatedBy(SecurityContext.currentUser());
    }
}
