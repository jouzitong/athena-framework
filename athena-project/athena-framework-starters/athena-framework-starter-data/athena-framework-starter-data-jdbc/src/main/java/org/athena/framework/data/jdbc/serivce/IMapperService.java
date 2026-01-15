package org.athena.framework.data.jdbc.serivce;

import org.arthena.framework.common.exception.NotSupportException;
import org.arthena.framework.common.exception.TodoException;
import org.athena.framework.data.jdbc.entity.IEntity;
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
public interface IMapperService<Entity extends IEntity> {

    /**
     * 列表查询
     *
     * @param query   查询条件
     * @param <Query> 查询条件类型
     * @return 查询结果
     */
    <Query extends BaseRequest> List<Entity> queryAll(Query query);

    /**
     * 分页查询
     *
     * @param query   查询条件
     * @param <Query> 查询条件类型
     * @return 分页结果
     */
    <Query extends BaseRequest> PageResultVO<Entity> page(Query query);

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
     * @param entity entity
     * @return entity
     */
    @Transactional(rollbackFor = Exception.class)
    Entity add(Entity entity);

    /**
     * 批量添加实体
     *
     * @param entities 待添加的实体列表
     * @return 成功添加的实体数量
     */
    int batchAdd(List<Entity> entities);

    /**
     * 保存或更新实体。
     * 如果实体ID存在，则执行更新操作；如果实体ID不存在，则执行插入操作。
     *
     * @param entity 要保存或更新的实体
     * @return 返回影响的行数，对于新增通常是1，对于更新则取决于实际更新了多少条记录
     * @throws TodoException 当该功能尚未实现时抛出此异常
     */
    default int saveOrUpdate(Entity entity) {
        throw new TodoException();
    }

    /**
     * 根据ID 更新全部的字段（允许设置null）
     *
     * @param dto DTO
     * @return 更新成功, 则返回DTO
     */
    @Transactional(rollbackFor = Exception.class)
    Entity update(Long id, Entity dto);

    /**
     * 批量更新实体列表
     *
     * @param entities 待更新的实体列表
     * @return 成功更新的实体数量
     */
    default int batchUpdate(List<Entity> entities) {
        throw new TodoException();
    }

    /**
     * 更新部分字段(不允许设置null)
     *
     * @param id  编号
     * @param dto DTO
     * @return DTO
     */
    @Transactional(rollbackFor = Exception.class)
    Entity edit(Long id, Entity dto);

    /**
     * 根据ID查询
     *
     * @param id 主键ID
     * @return DTO
     */
    Entity get(Long id);

    /**
     * 根据 query 查询
     * <p>
     * TODO 是不是应该判断如果是多个值, 就抛异常
     *
     * @param query   查询条件
     * @param <Query> 查询条件类型
     * @return 单个DTO
     */
    default <Query extends BaseRequest> Entity get(Query query) {
        query.setSize(2);
        List<Entity> dtos = queryAll(query);
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
    boolean delete(Long id);

    /**
     * 执行物理删除操作，从数据库中永久移除指定ID的实体。
     *
     * @param id 要删除的实体的ID
     * @return 如果删除成功，则返回true；否则返回false
     */
    default boolean physicalDelete(Long id) {
        throw new NotSupportException();
    }

    /**
     * 实例化Entity
     *
     * @return Entity对象
     */
    Entity newEntity();

    default Class<?> entityType() {
        return null;
    }

}
