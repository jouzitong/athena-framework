package org.athena.framework.data.jpa.domain;

import jakarta.persistence.MappedSuperclass;
import lombok.ToString;
import org.hibernate.annotations.SoftDelete;
import org.hibernate.annotations.SoftDeleteType;

@MappedSuperclass
@ToString(callSuper = true)
@SoftDelete(strategy = SoftDeleteType.DELETED)
public abstract class LogicalDeleteEntity extends AuditableEntity {

//    @Setter
//    @Getter
//    @Column(name = "deleted", nullable = false, columnDefinition = "tinyint(1) default 0 comment '是否删除, 0-未删除,1-已删除'")
//    private Boolean deleted = false;
}
