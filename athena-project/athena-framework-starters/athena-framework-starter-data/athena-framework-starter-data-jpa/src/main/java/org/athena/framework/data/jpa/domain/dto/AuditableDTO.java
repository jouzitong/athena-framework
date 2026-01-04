package org.athena.framework.data.jpa.domain.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

public abstract class AuditableDTO extends BaseDTO {

    @Getter
    @Setter
    private LocalDateTime createTime;

    @Getter
    @Setter
    private Long createdBy;

    @Getter
    @Setter
    private LocalDateTime UpdateTime;

    @Getter
    @Setter
    private Long updatedBy;
}
