package org.athena.framework.data.jpa.domain.base;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

@MappedSuperclass
@FilterDef(name = "logic_delete_filter", parameters = @ParamDef(name = "deleted", type = Boolean.class))
@Filter(name = "logic_delete_filter", condition = "deleted = :deleted")
@Data
public abstract class LogicalDeleteEntity extends AuditableEntity {

    @Column(nullable = false)
    private Boolean deleted = false;
}
