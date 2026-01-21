package org.athena.framework.data.jdbc.web;

import org.athena.framework.data.jdbc.convert.IConvert;
import org.athena.framework.data.jdbc.entity.IEntity;
import org.athena.framework.data.jdbc.entity.dto.IDTO;
import org.athena.framework.data.jdbc.req.BaseRequest;
import org.athena.framework.data.jdbc.serivce.IMapperService;
import org.athena.framework.data.jdbc.vo.PageResultVO;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public abstract class BaseController<Entity extends IEntity,
        DTO extends IDTO,
        Query extends BaseRequest,
        Service extends IMapperService<Entity>>
        implements IController<Entity, DTO, Query> {

    protected abstract Service service();

    protected abstract IConvert<Entity, DTO> convert();

    @Override
    public DTO add(@RequestBody DTO dto) {
        return toDTO(service().add(toEntity(dto)));
    }

    @Override
    public DTO update(Long id, @RequestBody DTO dto) {
        return toDTO(service().update(id, toEntity(dto)));
    }

    @Override
    public DTO edit(Long id, @RequestBody DTO dto) {
        return toDTO(service().edit(id, toEntity(dto)));
    }

    @Override
    public Boolean delete(Long id) {
        return service().delete(id);
    }

    @Override
    public Boolean physicalDelete(Long id) {
        return service().physicalDelete(id);
    }

    @Override
    public PageResultVO<DTO> page(Query query) {
        PageResultVO<Entity> page = service().page(query);
        List<Entity> data = page.getData();
        List<DTO> dtolist = data.stream().map(this::toDTO).toList();
        return PageResultVO.ok(dtolist, page.getPageInfo());
    }

    @Override
    public DTO get(Long id) {
        return toDTO(service().get(id));
    }

    protected Entity toEntity(DTO dto) {
        return convert().toEntity(dto);
    }

    protected DTO toDTO(Entity entity) {
        return convert().toDTO(entity);
    }

}
