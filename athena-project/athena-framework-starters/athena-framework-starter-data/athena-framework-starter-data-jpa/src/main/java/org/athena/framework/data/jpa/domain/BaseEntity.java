package org.athena.framework.data.jpa.domain;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.Setter;
import org.athena.framework.data.jdbc.entity.IEntity;

@MappedSuperclass
public abstract class BaseEntity implements IEntity {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, columnDefinition = "bigint comment '主键ID'")
    private Long id;

    @Getter
    @Setter
    @Version
    @Column(name = "version", nullable = false, columnDefinition = "bigint default 1 comment '版本'")
    private Long version;
}

