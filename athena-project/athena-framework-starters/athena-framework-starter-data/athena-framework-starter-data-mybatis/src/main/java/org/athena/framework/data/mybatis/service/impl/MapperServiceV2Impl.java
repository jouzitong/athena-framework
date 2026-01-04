package org.athena.framework.data.mybatis.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.athena.framework.data.jdbc.entity.IEntity;
import org.athena.framework.data.jdbc.req.BaseRequest;
import org.athena.framework.data.jdbc.vo.PageInfo;
import org.athena.framework.data.jdbc.vo.PageResultVO;
import org.athena.framework.data.mybatis.mapper.CrudMapper;
import org.athena.framework.data.mybatis.service.IMapperServiceV2;
import org.athena.framework.data.mybatis.utils.MybatisPlusWrapperUtils;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhouzhitong
 * @since 2025/8/6
 **/
@Slf4j
public class MapperServiceV2Impl<
        Entity extends IEntity,
        Mapper extends CrudMapper<Entity>>
        extends ServiceImpl<Mapper, Entity>
        implements IMapperServiceV2<Entity>, IService<Entity> {

    @Override
    public <Query extends BaseRequest> List<Entity> queryAll(Query query) {
        LOGGER.trace("queryAll request: {}", query);
        QueryWrapper<Entity> wrapper = buildQuery(query);
        return this.list(wrapper);
    }

    @Override
    public <Query extends BaseRequest> PageResultVO<Entity> page(Query query) {
        LOGGER.trace("page request: {}", query);
        QueryWrapper<Entity> wrapper = buildQuery(query);
        Page<Entity> objectPage = query.buildPage();
        List<Entity> records;
        // 在查询之前调用 PageHelper.startPage() 方法设置分页参数
        page(objectPage, wrapper);
        records = objectPage.getRecords();

        PageInfo pageInfo = new PageInfo(objectPage.getTotal(), query.size(), query.page());
        return PageResultVO.ok(records, pageInfo);
    }

    @Override
    public <Query extends BaseRequest> long count(Query query) {
        LOGGER.trace("count request: {}", query);
        return this.count(buildQuery(query));
    }

    @Override
    public Entity add(Entity entity) {
        LOGGER.info("add request: {}", entity);
        return save(entity) ? entity : null;
    }

    @Override
    public Entity update(Long id, Entity entity) {
        LOGGER.info("update request: {}", entity);
        boolean update = this.updateById(entity);
        return update ? entity : null;
    }

    @Override
    public Entity edit(Long id, Entity entity) {
        LOGGER.info("edit request: {}", entity);
        boolean update = this.updateById(entity);
        return update ? entity : null;
    }

    @Override
    public Entity get(Long id) {
        return this.getById(id);
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

}
