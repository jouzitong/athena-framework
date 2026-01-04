package org.athena.framework.data.jpa.domain.base;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import lombok.ToString;
import org.athena.framework.data.jpa.listener.AuditEntityListener;

import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners(AuditEntityListener.class)
@Data
@ToString(callSuper = true)
public abstract class AuditableEntity extends BaseEntity {

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @Column(updatable = false)
    private String createdBy;

    private LocalDateTime updatedAt;

    private String updatedBy;
}
