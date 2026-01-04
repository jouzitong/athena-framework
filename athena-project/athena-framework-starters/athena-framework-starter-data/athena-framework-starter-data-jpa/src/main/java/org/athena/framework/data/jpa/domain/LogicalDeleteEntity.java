package org.athena.framework.data.jpa.domain;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

@MappedSuperclass
@FilterDef(name = "logic_delete_filter", parameters = @ParamDef(name = "deleted", type = Boolean.class))
@Filter(name = "logic_delete_filter", condition = "deleted = :deleted")
@ToString(callSuper = true)
public abstract class LogicalDeleteEntity extends AuditableEntity {

    @Setter
    @Getter
    @Column(name = "deleted", nullable = false, columnDefinition = "bit(1) default 0 comment '是否删除, 0-未删除,1-已删除'")
    private Boolean deleted = false;
}
