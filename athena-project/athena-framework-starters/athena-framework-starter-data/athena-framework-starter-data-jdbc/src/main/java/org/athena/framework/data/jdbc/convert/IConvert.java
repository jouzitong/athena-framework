package org.athena.framework.data.jdbc.convert;

import org.athena.framework.data.jdbc.entity.IEntity;
import org.athena.framework.data.jdbc.entity.dto.IDTO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

//@Mapper(componentModel = "spring")
public interface IConvert<E extends IEntity, D extends IDTO> {

    D toDTO(E e);

    E toEntity(D d);

    @Mapping(target = "id", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
//    @Mapping(target = "updateTime", expression = "java(java.time.LocalDateTime.now())")
    void editEntityFromDto(D dto, @MappingTarget E entity);

    @Mapping(target = "id", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
    void updateEntityFromDto(D dto, @MappingTarget E entity);

}
