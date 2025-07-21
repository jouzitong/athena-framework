package org.athena.framework.data.mybatis.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.athena.framework.data.jdbc.req.BaseRequest;
import org.athena.framework.data.jdbc.vo.PageInfo;
import org.athena.framework.data.mybatis.dto.BaseDTO;
import org.athena.framework.data.mybatis.entity.BaseEntity;
import org.athena.framework.data.mybatis.mapper.CrudMapper;
import org.athena.framework.data.mybatis.service.MapperService;
import org.athena.framework.data.mybatis.utils.MybatisPlusWrapperUtils;
import org.athena.framework.data.mybatis.vo.PageResultVO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author zhouzhitong
 * @see MapperServiceImplV2 MapperServiceImplV2
 * @since 2022/9/28
 */
@Deprecated
@Slf4j
public abstract class MapperServiceImpl
        <Mapper extends CrudMapper<Entity>,
                Entity extends BaseEntity,
                DTO extends BaseDTO>
        extends ServiceImpl<Mapper, Entity>
        implements MapperService<Entity, DTO> {

    @Override
    public <Query extends BaseRequest> List<DTO> queryAll(Query query) {
        LOGGER.trace("queryAll request: {}", query);
        QueryWrapper<Entity> wrapper = buildQuery(query);
        List<Entity> list = this.list(wrapper);
        return list.stream().map(this::toDTO).toList();
    }

    @Override
    public <Query extends BaseRequest> PageResultVO<DTO> page(Query query) {
        LOGGER.trace("page request: {}", query);
        QueryWrapper<Entity> wrapper = buildQuery(query);
        Page<Entity> objectPage = query.buildPage();
        List<Entity> records;
        // 在查询之前调用 PageHelper.startPage() 方法设置分页参数
        page(objectPage, wrapper);
        records = objectPage.getRecords();

        PageInfo pageInfo = new PageInfo(objectPage.getTotal(), query.size(), query.page());
        List<DTO> dtoList = records.stream().map(this::toDTO).toList();
        return PageResultVO.ok(dtoList, pageInfo);
    }

    @Override
    public DTO add(DTO dto) {
        LOGGER.info("add request: {}", dto);
        // 创建一个实体
        Entity entity = newEntity();
        // 赋值
        copyProperties(dto, entity);
        // 保存
        return save(entity)
                ? toDTO(entity)
                : null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DTO update(Long id, DTO dto) {
        LOGGER.info("update request: {}", dto);
        Entity entity = getEntity(id);
        copyAllowNullProperties(dto, entity);

        boolean update = this.updateById(entity);
        return update
                ? toDTO(entity)
                : null;
    }

    @Override
    public DTO edit(Long id, DTO dto) {
        LOGGER.info("edit request: {}", dto);
        Entity entity = getEntity(id);
        copyProperties(dto, entity);

        boolean update = this.updateById(entity);
        return update
                ? toDTO(entity)
                : null;
    }

    @Override
    public <Query extends BaseRequest> long count(Query query) {
        LOGGER.trace("count request: {}", query);
        return this.count(buildQuery(query));
    }

    @Override
    public DTO get(Long id) {
        LOGGER.trace("get request: {}", id);
        Entity entity = getEntity(id);
        return toDTO(entity);
    }

    /**
     * 实例化Query
     *
     * @param query   查询条件
     * @param <Query> 查询条件类型
     * @return Query对象
     */
    protected <Query extends BaseRequest> QueryWrapper<Entity> buildQuery(Query query) {
        return MybatisPlusWrapperUtils.simpleQuery(query);
    }

    private Entity getEntity(Long id) {
        return this.getById(id);
    }

}
