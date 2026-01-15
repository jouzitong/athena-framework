package org.athena.framework.data.jpa.service;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.arthena.framework.common.utils.BeanUtils;
import org.athena.framework.data.jdbc.context.CrudContext;
import org.athena.framework.data.jdbc.executor.CrudInterceptorExecutor;
import org.athena.framework.data.jdbc.req.BaseRequest;
import org.athena.framework.data.jdbc.serivce.IMapperService;
import org.athena.framework.data.jdbc.type.DbOpType;
import org.athena.framework.data.jdbc.vo.PageInfo;
import org.athena.framework.data.jdbc.vo.PageResultVO;
import org.athena.framework.data.jpa.domain.BaseEntity;
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
public abstract class BaseMapperService<Entity extends BaseEntity>
        implements IMapperService<Entity> {

    @Resource
    private CrudInterceptorExecutor interceptorExecutor;

    @Override
    public <Query extends BaseRequest> List<Entity> queryAll(Query query) {
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
        LOGGER.trace("[queryAll] response: {}", entities.getContent());
        return entities.getContent();
    }

    @Override
    public <Query extends BaseRequest> PageResultVO<Entity> page(Query query) {
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
        List<Entity> dtoList = page.getContent();
        PageInfo pageInfo = new PageInfo(page.getTotalElements(), query.getSize(), query.getPage());
        return PageResultVO.ok(dtoList, pageInfo);
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
    public int batchAdd(List<Entity> entities) {
        LOGGER.info("batch add request: {}", entities);
        interceptorExecutor.beforeCheck(CrudContext.builder()
                .dbOpType(DbOpType.INSERT)
                .param(entities)
                .entityType(entityType())
                .build());
        Iterable<Entity> savedEntities = repository().saveAll(entities);
        interceptorExecutor.after(CrudContext.builder()
                .dbOpType(DbOpType.INSERT)
                .param(entities)
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
    public Entity add(Entity entity) {
        LOGGER.info("add data: {}", entity);
        interceptorExecutor.beforeCheck(CrudContext.builder()
                .dbOpType(DbOpType.INSERT)
                .param(entity)
                .entityType(entityType())
                .build());
        entity = repository().save(entity);
        interceptorExecutor.after(CrudContext.builder()
                .dbOpType(DbOpType.INSERT)
                .param(entity)
                .entityType(entityType())
                .result(entity)
                .build());
        LOGGER.debug("add data success: {}", entity);
        return entity;
    }

    @Override
    public Entity update(Long id, Entity dto) {
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
        BeanUtils.copy(dto, entity);
        interceptorExecutor.before(CrudContext.builder()
                .dbOpType(DbOpType.UPDATE)
                .param(dto)
                .entityType(entityType())
                .attributes(Map.of("oldEntity", entity))
                .build());
        entity = repository().save(entity);

        interceptorExecutor.after(CrudContext.builder()
                .dbOpType(DbOpType.UPDATE)
                .param(dto)
                .entityType(entityType())
                .result(entity)
                .attributes(Map.of("id", id))
                .build());
        LOGGER.debug("update data success: {}", entity);
        return entity;
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
    public Entity edit(Long id, Entity dto) {
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
        BeanUtils.copyForUpdate(dto, entity);

        interceptorExecutor.before(CrudContext.builder()
                .dbOpType(DbOpType.UPDATE)
                .param(dto)
                .entityType(entityType())
                .attributes(Map.of("oldEntity", entity))
                .build());
        entity = repository().save(entity);
        interceptorExecutor.after(CrudContext.builder()
                .dbOpType(DbOpType.UPDATE)
                .param(dto)
                .entityType(entityType())
                .result(entity)
                .attributes(Map.of("id", id))
                .build());
        LOGGER.debug("edit data success: {}", entity);
        return entity;
    }

    @Override
    public Entity get(Long id) {
        LOGGER.trace("get request: {}", id);
        return repository().findById(id).orElse(null);
    }

    protected abstract BaseRepository<Entity> repository();

}
