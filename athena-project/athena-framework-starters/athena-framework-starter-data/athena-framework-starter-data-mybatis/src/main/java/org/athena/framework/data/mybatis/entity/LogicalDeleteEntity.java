package org.athena.framework.data.mybatis.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Getter;
import lombok.Setter;

/**
 * 软删除实体基类。
 */
@Getter
@Setter
public abstract class LogicalDeleteEntity extends AuditableEntity {

    @TableLogic
    @TableField(value = "deleted")
    protected Integer deleted = 0;
}
