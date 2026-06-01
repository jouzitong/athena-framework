package org.athena.framework.data.jpa.service;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.arthena.framework.common.utils.JacksonJsonUtils;
import org.athena.framework.data.jdbc.context.CrudContext;
import org.athena.framework.data.jdbc.convert.IConvert;
import org.athena.framework.data.jdbc.entity.IEntity;
import org.athena.framework.data.jdbc.entity.dto.IDTO;
import org.athena.framework.data.jdbc.executor.CrudInterceptorExecutor;
import org.athena.framework.data.jdbc.req.BaseRequest;
import org.athena.framework.data.jdbc.serivce.IMapperService;
import org.athena.framework.data.jdbc.type.DbOpType;
import org.athena.framework.data.jdbc.vo.PageInfo;
import org.athena.framework.data.jdbc.vo.PageResultVO;
import org.athena.framework.data.jpa.repository.BaseRepository;
import org.athena.framework.data.jpa.utils.JpaQueryEngineUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public abstract class BaseMapperService<Entity extends IEntity, DTO extends IDTO>
        implements IMapperService<DTO> {

    protected abstract Class<?> entityType();

    protected abstract IConvert<Entity, DTO> convert();

    @Resource
    private CrudInterceptorExecutor interceptorExecutor;

    @Override
    public <Query extends BaseRequest> List<DTO> queryAll(Query query) {
        LOGGER.trace("[queryAll] request: {}", query);
        interceptorExecutor.beforeCheck(CrudContext.builder()
                .dbOpType(DbOpType.SELECT)
                .param(query)
                .entityType(entityType())
                .build());
        Specification<Entity> spec = JpaQueryEngineUtils.build(query);
        Pageable pageable = PageRequest.of(query.getPage() - 1, query.getSize());
        Page<Entity> entities = repository().findAll(spec, pageable);
        interceptorExecutor.after(CrudContext.builder()
                .dbOpType(DbOpType.SELECT)
                .param(query)
                .entityType(entityType())
                .result(entities)
                .build());
        List<DTO> dtos = entities.getContent().stream().map(convert()::toDTO).toList();
        LOGGER.trace("[queryAll] response: {}", dtos);
        return dtos;
    }

    @Override
    public <Query extends BaseRequest> PageResultVO<DTO> page(Query query) {
        LOGGER.trace("page request: {}", query);
        interceptorExecutor.beforeCheck(CrudContext.builder()
                .dbOpType(DbOpType.SELECT)
                .param(query)
                .entityType(entityType())
                .build());
        Pageable pageable = PageRequest.of(query.getPage() - 1, query.getSize());
        Specification<Entity> spec = JpaQueryEngineUtils.build(query);
        Page<Entity> page = repository().findAll(spec, pageable);
        interceptorExecutor.after(CrudContext.builder()
                .dbOpType(DbOpType.SELECT)
                .param(query)
                .entityType(entityType())
                .result(page)
                .build());
        List<DTO> dtoList = page.getContent().stream().map(convert()::toDTO).toList();
        PageInfo pageInfo = new PageInfo(page.getTotalElements(), query.getSize(), query.getPage());
        return PageResultVO.of(dtoList, pageInfo);
    }

    @Override
    public <Query extends BaseRequest> long count(Query query) {
        LOGGER.trace("count request: {}", query);
        interceptorExecutor.beforeCheck(CrudContext.builder()
                .dbOpType(DbOpType.SELECT)
                .param(query)
                .entityType(entityType())
                .build());
        Specification<Entity> spec = JpaQueryEngineUtils.build(query);
        long count = repository().count(spec);
        interceptorExecutor.after(CrudContext.builder()
                .dbOpType(DbOpType.SELECT)
                .param(query)
                .entityType(entityType())
                .result(count)
                .build());
        return count;
    }


    @Override
    public int batchAdd(List<DTO> dtos) {
        LOGGER.info("batch add request: {}", dtos);
        interceptorExecutor.beforeCheck(CrudContext.builder()
                .dbOpType(DbOpType.INSERT)
                .param(dtos)
                .entityType(entityType())
                .build());
        List<Entity> entities = dtos.stream().map(convert()::toEntity).toList();
        Iterable<Entity> savedEntities = repository().saveAll(entities);
        interceptorExecutor.after(CrudContext.builder()
                .dbOpType(DbOpType.INSERT)
                .param(dtos)
                .entityType(entityType())
                .result(savedEntities)
                .build());
        int count = 0;
        for (Entity entity : savedEntities) {
            if (entity != null) {
                count++;
            }
        }
        LOGGER.debug("batch add success, total added: {}", count);
        return count;
    }

    @Override
    public DTO add(DTO dto) {
        LOGGER.info("add data: {}", JacksonJsonUtils.toStr(dto));
        interceptorExecutor.beforeCheck(CrudContext.builder()
                .dbOpType(DbOpType.INSERT)
                .param(dto)
                .entityType(entityType())
                .build());
        Entity entity = convert().toEntity(dto);
        entity = repository().save(entity);
        DTO result = convert().toDTO(entity);
        interceptorExecutor.after(CrudContext.builder()
                .dbOpType(DbOpType.INSERT)
                .param(dto)
                .entityType(entityType())
                .result(result)
                .build());
        LOGGER.debug("add data success: {}", result);
        return result;
    }

    @Override
    public DTO update(Long id, DTO dto) {
        LOGGER.info("update request: {}", dto);
        interceptorExecutor.beforeCheck(CrudContext.builder()
                .dbOpType(DbOpType.UPDATE)
                .param(dto)
                .entityType(entityType())
                .attributes(Map.of("id", id))
                .build());
        Entity entity = repository().findById(id).orElse(null);
        if (entity == null) {
            return null;
        }
        interceptorExecutor.before(CrudContext.builder()
                .dbOpType(DbOpType.UPDATE)
                .param(dto)
                .entityType(entityType())
                .attributes(Map.of("oldEntity", entity))
                .build());
        convert().updateEntityFromDto(dto, entity);
        entity = repository().save(entity);
        DTO result = convert().toDTO(entity);

        interceptorExecutor.after(CrudContext.builder()
                .dbOpType(DbOpType.UPDATE)
                .param(dto)
                .entityType(entityType())
                .result(result)
                .attributes(Map.of("id", id))
                .build());
        LOGGER.debug("update data success: {}", result);
        return result;
    }

    @Override
    public boolean delete(Long id) {
        LOGGER.info("delete id: {}", id);
        interceptorExecutor.beforeCheck(CrudContext.builder()
                .dbOpType(DbOpType.DELETE)
                .param(id)
                .entityType(entityType())
                .build());
        repository().deleteById(id);
        interceptorExecutor.after(CrudContext.builder()
                .dbOpType(DbOpType.DELETE)
                .param(id)
                .entityType(entityType())
                .result(null)
                .build());
        return true;
    }

    @Override
    public DTO edit(Long id, DTO dto) {
        LOGGER.info("edit request: {}", dto);
        interceptorExecutor.beforeCheck(CrudContext.builder()
                .dbOpType(DbOpType.UPDATE)
                .param(dto)
                .entityType(entityType())
                .attributes(Map.of("id", id))
                .build());
        Entity entity = repository().findById(id).orElse(null);
        if (entity == null) {
            return null;
        }
        interceptorExecutor.before(CrudContext.builder()
                .dbOpType(DbOpType.UPDATE)
                .param(dto)
                .entityType(entityType())
                .attributes(Map.of("oldEntity", entity))
                .build());
        convert().editEntityFromDto(dto, entity);
        entity = repository().save(entity);
        DTO result = convert().toDTO(entity);
        interceptorExecutor.after(CrudContext.builder()
                .dbOpType(DbOpType.UPDATE)
                .param(dto)
                .entityType(entityType())
                .result(result)
                .attributes(Map.of("id", id))
                .build());
        LOGGER.debug("edit data success: {}", result);
        return result;
    }

    @Override
    public DTO get(Long id) {
        LOGGER.trace("get request: {}", id);
        return repository().findById(id).map(convert()::toDTO).orElse(null);
    }

    protected abstract BaseRepository<Entity> repository();

}
