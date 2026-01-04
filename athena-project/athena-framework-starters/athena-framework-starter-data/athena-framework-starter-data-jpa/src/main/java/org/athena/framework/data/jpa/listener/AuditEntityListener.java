package org.athena.framework.data.jpa.listener;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.extern.slf4j.Slf4j;
import org.arthena.framework.common.service.IUserContextService;
import org.athena.framework.data.jpa.domain.AuditableEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
public class AuditEntityListener {

    @Autowired
    private IUserContextService userContextService;

    @PrePersist
    public void preInsert(AuditableEntity entity) {
        entity.setCreateTime(LocalDateTime.now());
        entity.setCreatedBy(userContextService.getUserId());
    }

    @PreUpdate
    public void preUpdate(AuditableEntity entity) {
        entity.setUpdateTime(LocalDateTime.now());
        entity.setUpdatedBy(userContextService.getUserId());
    }
}
