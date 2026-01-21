package org.athena.framework.data.jpa.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
public abstract class AuditableDTO extends BaseDTO {

    @Getter
    @Setter
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Getter
    @Setter
    private Long createdBy;

    @Getter
    @Setter
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    @Getter
    @Setter
    private Long updatedBy;
}
