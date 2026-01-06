package org.athena.framework.data.jdbc.entity.convert;

import org.athena.framework.data.jdbc.entity.IEntity;
import org.athena.framework.data.jdbc.entity.dto.IDTO;
import org.mapstruct.Mapper;

//@Mapper(componentModel = "spring")
public interface IEntityConvert<E extends IEntity, D extends IDTO> {

    D toDTO(E e);

    E toEntity(D d);

}
