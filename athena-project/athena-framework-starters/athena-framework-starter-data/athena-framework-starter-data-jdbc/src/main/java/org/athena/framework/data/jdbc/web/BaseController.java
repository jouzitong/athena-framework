package org.athena.framework.data.jdbc.web;

import jakarta.annotation.Resource;
import org.arthena.framework.common.utils.BeanUtils;
import org.athena.framework.data.jdbc.entity.IEntity;
import org.athena.framework.data.jdbc.entity.dto.IDTO;
import org.athena.framework.data.jdbc.req.BaseRequest;
import org.athena.framework.data.jdbc.serivce.IMapperService;
import org.athena.framework.data.jdbc.vo.PageResultVO;

import java.util.List;

public abstract class BaseController<Entity extends IEntity,
        DTO extends IDTO,
        Query extends BaseRequest,
        Service extends IMapperService<Entity>>
        implements IController<Entity, DTO, Query> {

    @Resource
    private Service service;

    protected Service service() {
        return service;
    }

    @Override
    public DTO add(DTO dto) {
        return toDTO(service.add(toEntity(dto)));
    }

    @Override
    public DTO update(Long id, DTO dto) {
        return toDTO(service.update(id, toEntity(dto)));
    }

    @Override
    public DTO edit(Long id, DTO dto) {
        return toDTO(service.edit(id, toEntity(dto)));
    }

    @Override
    public Boolean delete(Long id) {
        return service.remove(id);
    }

    @Override
    public PageResultVO<DTO> page(Query query) {
        PageResultVO<Entity> page = service.page(query);
        List<Entity> data = page.getData();
        List<DTO> dtolist = data.stream().map(this::toDTO).toList();
        return PageResultVO.ok(dtolist, page.getPageInfo());
    }

    @Override
    public DTO get(Long id) {
        return toDTO(service.get(id));
    }

    protected Entity toEntity(DTO dto) {
        Entity entity = newEntity();
        BeanUtils.copy(dto, entity);
        return entity;
    }

    protected DTO toDTO(Entity entity) {
        DTO dto = newDTO();
        BeanUtils.copy(entity, dto);
        return dto;
    }

    protected abstract Entity newEntity();

    protected abstract DTO newDTO();

}
