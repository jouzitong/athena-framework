package org.athena.framework.data.jpa.service;

import lombok.extern.slf4j.Slf4j;
import org.arthena.framework.common.utils.BeanUtils;
import org.athena.framework.data.jdbc.entity.IEntity;
import org.athena.framework.data.jdbc.entity.dto.IDTO;
import org.athena.framework.data.jdbc.req.BaseRequest;
import org.athena.framework.data.jdbc.serivce.IMapperService;
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
import java.util.stream.Collectors;

@Slf4j
@Service
public abstract class BaseMapperService<Entity extends IEntity, DTO extends IDTO>
        implements IMapperService<Entity, DTO> {

    @Override
    public <Query extends BaseRequest> List<DTO> queryAll(Query query) {
        LOGGER.trace("queryAll request: {}", query);
        Specification<Entity> spec = JpaQueryEngineUtils.build(query);
        Pageable pageable = PageRequest.of(query.getPage(), query.getSize());
        Page<Entity> entities = repository().findAll(spec, pageable);
        return entities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public <Query extends BaseRequest> PageResultVO<DTO> page(Query query) {
        LOGGER.trace("page request: {}", query);
        Pageable pageable = PageRequest.of(query.getPage(), query.getSize());
        Specification<Entity> spec = JpaQueryEngineUtils.build(query);
        Page<Entity> page = repository().findAll(spec, pageable);
        List<DTO> dtoList = page.getContent().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
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
    public DTO add(DTO dto) {
        LOGGER.info("add data: {}", dto);
        Entity entity = newEntity();
        copyProperties(dto, entity);
        entity = repository().save(entity);
        LOGGER.info("add data success: {}", entity);
        return toDTO(entity);
    }

    @Override
    public DTO update(Long id, DTO dto) {
        LOGGER.info("update request: {}", dto);
        Entity entity = repository().findById(id).orElse(null);
        if (entity == null) {
            return null;
        }
        copyAllowNullProperties(dto, entity);
        entity = repository().save(entity);
        LOGGER.info("update data success: {}", entity);
        return toDTO(entity);
    }

    @Override
    public DTO edit(Long id, DTO dto) {
        LOGGER.info("edit request: {}", dto);
        Entity entity = repository().findById(id).orElse(null);
        if (entity == null) {
            return null;
        }
        copyProperties(dto, entity);
        entity = repository().save(entity);
        LOGGER.info("edit data success: {}", entity);
        return toDTO(entity);
    }

    @Override
    public DTO get(Long id) {
        LOGGER.trace("get request: {}", id);
        Entity entity = repository().findById(id).orElse(null);
        return toDTO(entity);
    }

    protected abstract BaseRepository<Entity, Long> repository();

    /**
     * DTO转Entity
     *
     * @param dto    DTO
     * @param entity Entity
     */
    protected void copyProperties(DTO dto, Entity entity) {
        BeanUtils.copy(dto, entity);
//        entity.getAndIncrementVersion();
    }

    /**
     * DTO转Entity
     *
     * @param dto    DTO
     * @param entity Entity
     */
    protected void copyAllowNullProperties(DTO dto, Entity entity) {
        BeanUtils.copyForUpdate(dto, entity);
//        entity.getAndIncrementVersion();
    }

}
