package org.athena.framework.data.jpa.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.athena.framework.data.jdbc.entity.dto.IDTO;

@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseDTO implements IDTO {

    @Getter
    @Setter
    private Long id;

    @Getter
    @Setter
    private Long version;
}

