package org.athena.framework.data.mybatis.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.athena.framework.data.jdbc.entity.BaseEntity;
import org.athena.framework.data.jdbc.req.BaseRequest;
import org.athena.framework.data.jdbc.serivce.IMapperService;
import org.athena.framework.data.jdbc.vo.PageInfo;
import org.athena.framework.data.jdbc.vo.PageResultVO;
import org.athena.framework.data.jdbc.dto.BaseDTO;
import org.athena.framework.data.mybatis.mapper.CrudMapper;
import org.athena.framework.data.mybatis.utils.MybatisPlusWrapperUtils;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhouzhitong
 * @since 2025/7/13
 **/
@Slf4j
public abstract class MapperServiceImpl<
        Entity extends BaseEntity,
        Mapper extends CrudMapper<Entity>,
        DTO extends BaseDTO,
        ID extends Serializable>
        extends ServiceImpl<Mapper, Entity>
        implements IMapperService<Entity, DTO, ID>, IService<Entity> {

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
    public <Query extends BaseRequest> long count(Query query) {
        LOGGER.trace("count request: {}", query);
        return this.count(buildQuery(query));
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
    public DTO update(ID id, DTO dto) {
        LOGGER.info("update request: {}", dto);
        Entity entity = getEntity(id);
        copyAllowNullProperties(dto, entity);

        boolean update = this.updateById(entity);
        return update
                ? toDTO(entity)
                : null;
    }

    @Override
    public DTO edit(ID id, DTO dto) {
        LOGGER.info("edit request: {}", dto);
        Entity entity = getEntity(id);
        copyProperties(dto, entity);

        boolean update = this.updateById(entity);
        return update
                ? toDTO(entity)
                : null;
    }

    @Override
    public DTO get(ID id) {
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

    private Entity getEntity(ID id) {
        return this.getById(id);
    }

}
