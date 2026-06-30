package org.athena.framework.test.storage.mybatis.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;
import org.athena.framework.data.mybatis.annotations.DdlColumnLength;
import org.athena.framework.data.mybatis.entity.LogicalDeleteEntity;

/**
 * 测试计划实体。
 */
@Getter
@Setter
@TableName("athena_test_plan")
public class TestPlanEntity extends LogicalDeleteEntity {

    @TableField("plan_code")
    @DdlColumnLength(64)
    private String planCode;

    @TableField("name")
    @DdlColumnLength(128)
    private String name;

    @TableField("description")
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @TableField("status")
    @DdlColumnLength(32)
    private String status;

    @TableField("cron_expression")
    @DdlColumnLength(128)
    private String cronExpression;

    @TableField("default_version")
    @DdlColumnLength(64)
    private String defaultVersion;

    @TableField("last_triggered_at")
    private LocalDateTime lastTriggeredAt;

    @TableField("next_trigger_at")
    private LocalDateTime nextTriggerAt;
}
