package org.athena.framework.data.jdbc.web;

import jakarta.annotation.Resource;
import org.athena.framework.data.jdbc.entity.IEntity;
import org.athena.framework.data.jdbc.entity.dto.IDTO;
import org.athena.framework.data.jdbc.req.BaseRequest;
import org.athena.framework.data.jdbc.serivce.IMapperService;
import org.athena.framework.data.jdbc.vo.PageResultVO;

public class BaseController<Entity extends IEntity,
        DTO extends IDTO,
        Query extends BaseRequest, Service extends IMapperService<Entity, DTO>>
        implements IController<Entity, DTO, Query> {

    @Resource
    private Service service;

    protected Service service() {
        return service;
    }

    @Override
    public DTO add(DTO dto) {
        return service.add(dto);
    }

    @Override
    public DTO update(Long id, DTO dto) {
        return service.update(id, dto);
    }

    @Override
    public DTO edit(Long id, DTO dto) {
        return service.edit(id, dto);
    }

    @Override
    public Boolean delete(Long id) {
        return service.remove(id);
    }

    @Override
    public PageResultVO<DTO> page(Query query) {
        return service.page(query);
    }

    @Override
    public DTO get(Long id) {
        return service.get(id);
    }
}
