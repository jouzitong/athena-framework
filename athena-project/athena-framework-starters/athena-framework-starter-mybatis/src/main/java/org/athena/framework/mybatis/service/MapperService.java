package org.athena.framework.mybatis.service;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import org.arthena.framework.common.context.SystemContext;
import org.arthena.framework.common.utils.BeanUtils;
import org.athena.framework.mybatis.base.BaseRequest;
import org.athena.framework.mybatis.dto.BaseDTO;
import org.athena.framework.mybatis.entity.BaseEntity;
import org.athena.framework.mybatis.utils.MybatisPlusWrapperUtils;
import org.athena.framework.mybatis.vo.PageResultVO;

import java.time.LocalDateTime;
import java.util.List;

/**
 * CRUD 基础服务类
 *
 * @author zhouzhitong
 * @since 2022/9/28
 */
public interface MapperService<
        Entity extends BaseEntity
        , DTO extends BaseDTO
        > extends IService<Entity> {

    /**
     * 分页查询
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
    DTO add(DTO dto);

    /**
     * 更新全部的字段
     *
     * @param id  编号
     * @param dto DTO
     * @return DTO
     */
    DTO update(Long id, DTO dto);

    /**
     * 更新部分字段
     *
     * @param id 编号
     * @param dto DTO
     * @return  DTO
     */
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
    default <Query extends BaseRequest> DTO getOne(Query query) {
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
    default boolean remove(Long id) {
        Entity entity = getById(id);
        entity.setDeleted(1);
        entity.setUpdateTime(LocalDateTime.now());
        entity.setLastModifiedBy(SystemContext.getUserId());
        return updateById(entity);
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

    /**
     * 实例化Query
     *
     * @param query   查询条件
     * @param <Query> 查询条件类型
     * @return Query对象
     */
    default <Query extends BaseRequest> QueryWrapper<Entity> buildQuery(Query query) {
        return MybatisPlusWrapperUtils.simpleQuery(query);
    }

    /**
     * 编号生成器
     *
     * @return 编号
     */
    default String generateNo() {
        return RandomUtil.randomString(16);
    }

}
