package org.athena.framework.data.jdbc.web;

import org.athena.framework.data.jdbc.entity.dto.IDTO;
import org.athena.framework.data.jdbc.req.BaseRequest;
import org.athena.framework.data.jdbc.serivce.IMapperService;
import org.athena.framework.data.jdbc.vo.PageResultVO;
import org.springframework.web.bind.annotation.RequestBody;

public abstract class BaseController<DTO extends IDTO, Query extends BaseRequest, Service extends IMapperService<DTO>>
        implements IController<DTO, Query> {

    protected abstract Service service();

    @Override
    public DTO add(@RequestBody DTO dto) {
        return service().add(dto);
    }

    @Override
    public DTO update(Long id, @RequestBody DTO dto) {
        return service().update(id, dto);
    }

    @Override
    public DTO edit(Long id, @RequestBody DTO dto) {
        return service().edit(id, dto);
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
        return service().page(query);
    }

    @Override
    public PageResultVO<DTO> pageSearch(Query query) {
        return service().page(query);
    }

    @Override
    public DTO get(Long id) {
        return service().get(id);
    }

}
