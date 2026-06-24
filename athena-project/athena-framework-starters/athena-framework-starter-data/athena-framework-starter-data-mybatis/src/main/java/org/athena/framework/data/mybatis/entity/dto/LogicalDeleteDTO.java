package org.athena.framework.data.mybatis.entity.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 软删除 DTO 基类。
 */
@Getter
@Setter
@ToString(callSuper = true)
public abstract class LogicalDeleteDTO extends AuditableDTO {

    private Integer deleted = 0;
}
