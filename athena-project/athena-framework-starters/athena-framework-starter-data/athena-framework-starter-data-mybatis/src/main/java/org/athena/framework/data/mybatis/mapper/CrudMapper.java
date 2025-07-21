package org.athena.framework.data.mybatis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.athena.framework.data.jdbc.entity.IEntity;

/**
 * 基础的crud, 所有的 mapper 都应该继承它
 *
 * @author zhouzhitong
 * @since 2022/9/25
 */
public interface CrudMapper<Entity extends IEntity> extends BaseMapper<Entity> {

}
