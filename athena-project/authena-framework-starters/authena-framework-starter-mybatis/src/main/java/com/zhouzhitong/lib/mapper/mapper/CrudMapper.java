package com.zhouzhitong.lib.mapper.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhouzhitong.lib.mapper.entity.BaseEntity;

/**
 * 基础的crud, 所有的 mapper 都应该继承它
 *
 * @author zhouzhitong
 * @since 2022/9/25
 */
public interface CrudMapper<Entity extends BaseEntity> extends BaseMapper<Entity> {

}
