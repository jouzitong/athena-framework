package org.athena.framework.data.mybatis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.Getter;
import lombok.Setter;
import org.athena.framework.data.jdbc.entity.IEntity;

import java.io.Serial;

/**
 * MyBatis 实体基类，仅承载主键和乐观锁版本号。
 *
 * @author zhouzhitong
 * @since 2022-09-07
 */
@Getter
@Setter
public abstract class BaseEntity implements IEntity {

    @Serial
    private static final long serialVersionUID = 8328293151203544834L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    protected Long id;

    /**
     * 版本
     */
    @Version
    @TableField(value = "version")
    protected Long version = 1L;

//    /**
//     * 租户ID
//     */
//    @TableField(value = "tenant_id")
//    protected Long tenantId;
}
