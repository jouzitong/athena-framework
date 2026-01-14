package org.athena.framework.data.jdbc.serivce;

import org.arthena.framework.common.utils.BeanUtils;
import org.athena.framework.data.jdbc.entity.IEntity;
import org.athena.framework.data.jdbc.entity.dto.IDTO;
import org.athena.framework.data.jdbc.req.BaseRequest;
import org.athena.framework.data.jdbc.vo.PageResultVO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * CRUD 基础服务类
 *
 * @author zhouzhitong
 * @since 2022/9/28
 */
@Deprecated
public interface IMapperServiceV2<Entity extends IEntity, DTO extends IDTO> {

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
     * 根据ID 更新全部的字段（允许设置null）
     *
     * @param dto DTO
     * @return 更新成功, 则返回DTO
     */
    @Transactional(rollbackFor = Exception.class)
    DTO update(Long id, DTO dto);

    /**
     * 更新部分字段(不允许设置null)
     *
     * @param id  编号
     * @param dto DTO
     * @return DTO
     */
    @Transactional(rollbackFor = Exception.class)
    DTO edit(Long id, DTO dto);

    /**
     * 根据ID查询
     *
     * @param id 主键ID
     * @return DTO
     */
    DTO get(Long id);

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
        query.setSize(2);
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
    default boolean remove(Long id) {
        DTO dto = get(id);
//        dto.setDeleted(true);
        return update(id, dto) != null;
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


}
