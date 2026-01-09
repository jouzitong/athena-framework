package org.athena.framework.data.jdbc.entity.convert;

import org.athena.framework.data.jdbc.entity.IEntity;
import org.athena.framework.data.jdbc.entity.dto.IDTO;

//@Mapper(componentModel = "spring")
public interface IConvert<E extends IEntity, D extends IDTO> {

    D toDTO(E e);

    E toEntity(D d);

}
