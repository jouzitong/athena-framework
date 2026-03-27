package org.athena.framework.data.jdbc.convert;

import org.athena.framework.data.jdbc.entity.IEntity;
import org.athena.framework.data.jdbc.entity.dto.IDTO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

//@Mapper(componentModel = "spring")
/**
 * 通用转换接口：用于在数据库实体（Entity）和数据传输对象（DTO）之间进行相互转换。
 *
 * <p>泛型说明：
 * - E 表示实体类型，必须实现 {@link org.athena.framework.data.jdbc.entity.IEntity}
 * - D 表示 DTO 类型，必须实现 {@link org.athena.framework.data.jdbc.entity.dto.IDTO}
 *
 * <p>实现该接口通常配合 MapStruct 使用（接口上可添加 {@code @Mapper(componentModel = "spring")}），
 * 通过注解定义字段映射和映射行为。接口中的部分方法使用了 MapStruct 的注解来控制合并策略，
 * 实现类无需手动编写重复的映射代码。
 */
public interface IConvert<E extends IEntity, D extends IDTO> {

    /**
     * 将实体对象转换为 DTO。
     *
     * @param e 要转换的实体对象，允许为 {@code null}（具体实现可决定是否支持 null）
     * @return 转换得到的 DTO 对象，或 {@code null}（如果输入为 {@code null} 或者转换产生空值）
     */
    D toDTO(E e);
    
    /**
     * 将 DTO 转换为实体对象。
     *
     * @param d 要转换的 DTO 对象
     * @return 转换得到的实体对象
     */
    E toEntity(D d);

    /**
     * 使用 DTO 的值去编辑已有的实体（部分更新/忽略空值）。
     *
     * <p>注意：该方法使用了 MapStruct 注解：
     * - {@code @Mapping(target = "id", ignore = true)}：忽略 id 字段的映射，避免修改主键。
     * - {@code @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)}：
     *   当 DTO 中某个属性为 {@code null} 时，保留实体原有值（即忽略 null 值），适用于增量修改场景。
     *
     * @param dto   提供更新数据的 DTO
     * @param entity 目标实体，更新会直接应用到该对象上（通过 {@code @MappingTarget} 表示）
     */
    @Mapping(target = "id", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
//    @Mapping(target = "updateTime", expression = "java(java.time.LocalDateTime.now())")
    void editEntityFromDto(D dto, @MappingTarget E entity);

    /**
     * 使用 DTO 的值去更新已有的实体（将 DTO 中的 {@code null} 值也应用到实体上，可能会将字段置空）。
     *
     * <p>注意：该方法使用了 MapStruct 注解：
     * - {@code @Mapping(target = "id", ignore = true)}：忽略 id 字段的映射，避免修改主键。
     * - {@code @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)}：
     *   当 DTO 中某个属性为 {@code null} 时，会把实体对应属性设置为 {@code null}，适用于覆盖式更新场景。
     *
     * @param dto    提供更新数据的 DTO
     * @param entity 目标实体，更新会直接应用到该对象上（通过 {@code @MappingTarget} 表示）
     */
    @Mapping(target = "id", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
    void updateEntityFromDto(D dto, @MappingTarget E entity);

}
