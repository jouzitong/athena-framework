package org.athena.framework.data.jdbc.web;

import com.fasterxml.jackson.core.type.TypeReference;
import org.arthena.framework.common.utils.BeanUtils;
import org.arthena.framework.common.utils.JacksonJsonUtils;
import org.athena.framework.data.jdbc.entity.IEntity;
import org.athena.framework.data.jdbc.entity.dto.IDTO;
import org.athena.framework.data.jdbc.req.BaseRequest;
import org.athena.framework.data.jdbc.serivce.IMapperService;
import org.athena.framework.data.jdbc.vo.PageResultVO;

import java.lang.reflect.Type;
import java.util.List;

public abstract class BaseController<Entity extends IEntity,
        DTO extends IDTO,
        Query extends BaseRequest,
        Service extends IMapperService<Entity>>
        implements IController<Entity, DTO, Query> {

    protected abstract Service service();

    @Override
    public DTO add(DTO dto) {
        return toDTO(service().add(toEntity(dto)));
    }

    @Override
    public DTO update(Long id, DTO dto) {
        return toDTO(service().update(id, toEntity(dto)));
    }

    @Override
    public DTO edit(Long id, DTO dto) {
        return toDTO(service().edit(id, toEntity(dto)));
    }

    @Override
    public Boolean delete(Long id) {
        return service().remove(id);
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
        String content = JacksonJsonUtils.toStr(dto);
        return JacksonJsonUtils.toBean(content, new TypeReference<>() {});
    }

    protected DTO toDTO(Entity entity) {
        String content = JacksonJsonUtils.toStr(entity);
        return JacksonJsonUtils.toBean(content, new TypeReference<>() {});
    }

    protected abstract Entity newEntity();

    protected abstract DTO newDTO();

}
