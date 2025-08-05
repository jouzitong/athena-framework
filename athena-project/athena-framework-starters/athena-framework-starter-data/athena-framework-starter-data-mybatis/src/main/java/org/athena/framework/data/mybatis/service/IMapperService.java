package org.athena.framework.data.mybatis.service;

import org.arthena.framework.common.exception.TodoException;
import org.arthena.framework.common.utils.BeanUtils;
import org.athena.framework.data.jdbc.dto.IDTO;
import org.athena.framework.data.jdbc.entity.IEntity;
import org.athena.framework.data.jdbc.req.BaseRequest;
import org.athena.framework.data.jdbc.vo.PageResultVO;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

/**
 * CRUD 基础服务类
 *
 * @author zhouzhitong
 * @since 2022/9/28
 */
public interface IMapperService<Entity extends IEntity, DTO extends IDTO, ID extends Serializable> {

    /**
     * 列表查询
     *
     * @param query   查询条件
     * @param <Query> 查询条件类型
     * @return 查询结果
     */
    <Query extends BaseRequest> List<DTO> queryAll(Query query);

    /**
     * 分页查询
     *
     * @param query   查询条件
     * @param <Query> 查询条件类型
     * @return 分页结果
     */
    <Query extends BaseRequest> PageResultVO<DTO> page(Query query);

    /**
     * 查询总数
     *
     * @param query   查询条件
     * @param <Query> 查询条件类型
     * @return 总数
     */
    <Query extends BaseRequest> long count(Query query);


    /**
     * 新增
     *
     * @param dto DTO
     * @return DTO
     */
    @Transactional(rollbackFor = Exception.class)
    DTO add(DTO dto);

    /**
     * 根据ID 更新全部的字段
     *
     * @param dto DTO
     * @return 更新成功, 则返回DTO
     */
    @Transactional(rollbackFor = Exception.class)
    DTO update(ID id, DTO dto);

    /**
     * 更新部分字段
     *
     * @param id  编号
     * @param dto DTO
     * @return DTO
     */
    @Transactional(rollbackFor = Exception.class)
    DTO edit(ID id, DTO dto);

    /**
     * 根据ID查询
     *
     * @param id 主键ID
     * @return DTO
     */
    DTO get(ID id);

    /**
     * 根据 query 查询
     * <p>
     * TODO 是不是应该判断如果是多个值, 就抛异常
     *
     * @param query   查询条件
     * @param <Query> 查询条件类型
     * @return 单个DTO
     */
    default <Query extends BaseRequest> DTO get(Query query) {
        List<DTO> dtos = queryAll(query);
        if (dtos.isEmpty()) {
            return null;
        }
//        if (dtos.size() > 1) {
//            throw new IllegalFormatCodePointException();
//        }
        return dtos.get(0);
    }

    /**
     * 软删除
     *
     * @param id 编号
     * @return 是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    default boolean remove(ID id) {
        DTO dto = get(id);
        dto.setDeleted(true);
        return update(id, dto) != null;
    }

    /**
     * Entity转DTO
     *
     * @param entity Entity
     * @return DTO
     */
    default DTO toDTO(Entity entity) {
        if (entity == null) {
            return null;
        }
        DTO dto = newDTO();
        BeanUtils.copy(entity, dto);
        return dto;
    }

    /**
     * DTO转Entity
     *
     * @param dto    DTO
     * @param entity Entity
     */
    default void copyProperties(DTO dto, Entity entity) {
        BeanUtils.copy(dto, entity);
        entity.getAndIncrementVersion();
    }

    /**
     * DTO转Entity
     *
     * @param dto    DTO
     * @param entity Entity
     */
    default void copyAllowNullProperties(DTO dto, Entity entity) {
        BeanUtils.copyForUpdate(dto, entity);
        entity.getAndIncrementVersion();
    }

    /**
     * 实例化DTO
     *
     * @return DTO对象
     */
    DTO newDTO();

    /**
     * 实例化Entity
     *
     * @return Entity对象
     */
    Entity newEntity();

    default Class<Entity> getEntityClass() {
        return (Class<Entity>) newEntity().getClass();
    }

    /**
     * 编号生成器
     *
     * @return 编号
     */
    default String generateNo() {
        throw new TodoException();
    }

}
