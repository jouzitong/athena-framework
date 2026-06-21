package org.athena.framework.data.mybatis.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.athena.framework.data.mybatis.annotations.DdlIgnoreTable;

import java.time.LocalDateTime;

/**
 * 审计字段基类。
 */
@Getter
@Setter
@DdlIgnoreTable
public abstract class AuditableEntity extends BaseEntity {

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    protected LocalDateTime createTime;

    @TableField(value = "created_by", fill = FieldFill.INSERT)
    protected Long createdBy = -1L;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    protected LocalDateTime updateTime;

    @TableField(value = "updated_by", fill = FieldFill.INSERT_UPDATE)
    protected Long updatedBy = -1L;
}
