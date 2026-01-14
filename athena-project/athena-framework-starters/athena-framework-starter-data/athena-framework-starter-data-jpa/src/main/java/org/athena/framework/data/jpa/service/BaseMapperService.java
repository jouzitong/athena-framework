package org.athena.framework.data.jpa.service;

import lombok.extern.slf4j.Slf4j;
import org.arthena.framework.common.utils.BeanUtils;
import org.athena.framework.data.jdbc.req.BaseRequest;
import org.athena.framework.data.jdbc.serivce.IMapperService;
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

@Slf4j
@Service
public abstract class BaseMapperService<Entity extends BaseEntity>
        implements IMapperService<Entity> {

    @Override
    public <Query extends BaseRequest> List<Entity> queryAll(Query query) {
        LOGGER.trace("[queryAll] request: {}", query);
        Specification<Entity> spec = JpaQueryEngineUtils.build(query);
        Pageable pageable = PageRequest.of(query.getPage() - 1, query.getSize());
        Page<Entity> entities = repository().findAll(spec, pageable);
        LOGGER.trace("[queryAll] response: {}", entities.getContent());
        return entities.getContent();
    }

    @Override
    public <Query extends BaseRequest> PageResultVO<Entity> page(Query query) {
        LOGGER.trace("page request: {}", query);
        Pageable pageable = PageRequest.of(query.getPage() - 1, query.getSize());
        Specification<Entity> spec = JpaQueryEngineUtils.build(query);
        Page<Entity> page = repository().findAll(spec, pageable);
        List<Entity> dtoList = page.getContent();
        PageInfo pageInfo = new PageInfo(page.getTotalElements(), query.getSize(), query.getPage());
        return PageResultVO.ok(dtoList, pageInfo);
    }

    @Override
    public <Query extends BaseRequest> long count(Query query) {
        LOGGER.trace("count request: {}", query);
        Specification<Entity> spec = JpaQueryEngineUtils.build(query);
        return repository().count(spec);
    }


    @Override
    public int batchAdd(List<Entity> entities) {
        LOGGER.info("batch add request: {}", entities);
        Iterable<Entity> savedEntities = repository().saveAll(entities);
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
        entity = repository().save(entity);
        LOGGER.debug("add data success: {}", entity);
        return entity;
    }

    @Override
    public Entity update(Long id, Entity dto) {
        LOGGER.info("update request: {}", dto);
        Entity entity = repository().findById(id).orElse(null);
        if (entity == null) {
            return null;
        }
        BeanUtils.copy(dto, entity);
        entity = repository().save(dto);
        LOGGER.debug("update data success: {}", entity);
        return entity;
    }

    @Override
    public boolean remove(Long id) {
        LOGGER.info("delete id: {}", id);
        repository().deleteById(id);
        return true;
    }

    @Override
    public Entity edit(Long id, Entity dto) {
        LOGGER.info("edit request: {}", dto);
        Entity entity = repository().findById(id).orElse(null);
        if (entity == null) {
            return null;
        }
        BeanUtils.copyForUpdate(dto, entity);
        entity = repository().save(entity);
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
