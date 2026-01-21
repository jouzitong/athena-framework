package org.athena.framework.data.jpa.domain.dto;

import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString(callSuper = true)
@NoArgsConstructor
public abstract class LogicalDeleteDTO extends AuditableDTO {

//    @Setter
//    @Getter
//    private Boolean deleted = false;
}
