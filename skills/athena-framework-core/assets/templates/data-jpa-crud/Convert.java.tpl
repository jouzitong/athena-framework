package {{PACKAGE}}.service;

import {{PACKAGE}}.model.{{NAME}}DTO;
import {{PACKAGE}}.persistence.{{NAME}}Entity;
import org.athena.framework.data.jdbc.convert.IConvert;
import org.springframework.stereotype.Component;

@Component
public class {{NAME}}Convert implements IConvert<{{NAME}}Entity, {{NAME}}DTO> {

    @Override
    public {{NAME}}DTO toDTO({{NAME}}Entity entity) {
        if (entity == null) {
            return null;
        }
        {{NAME}}DTO dto = new {{NAME}}DTO();
        dto.setId(entity.getId());
        dto.setVersion(entity.getVersion());
        dto.setName(entity.getName());
        return dto;
    }

    @Override
    public {{NAME}}Entity toEntity({{NAME}}DTO dto) {
        if (dto == null) {
            return null;
        }
        {{NAME}}Entity entity = new {{NAME}}Entity();
        entity.setId(dto.getId());
        entity.setVersion(dto.getVersion());
        entity.setName(dto.getName());
        return entity;
    }

    @Override
    public void editEntityFromDto({{NAME}}DTO dto, {{NAME}}Entity entity) {
        if (dto.getName() != null) {
            entity.setName(dto.getName());
        }
    }

    @Override
    public void updateEntityFromDto({{NAME}}DTO dto, {{NAME}}Entity entity) {
        entity.setName(dto.getName());
    }
}
