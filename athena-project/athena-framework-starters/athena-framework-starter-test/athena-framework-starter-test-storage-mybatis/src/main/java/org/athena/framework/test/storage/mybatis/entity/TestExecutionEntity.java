package org.athena.framework.test.storage.mybatis.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.persistence.Column;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.athena.framework.data.mybatis.annotations.DdlColumnLength;
import org.athena.framework.data.mybatis.entity.AuditableEntity;

/**
 * 测试执行记录实体。
 */
@Getter
@Setter
@TableName("athena_test_execution")
public class TestExecutionEntity extends AuditableEntity {

    @TableField("execution_no")
    @DdlColumnLength(64)
    private String executionNo;

    @TableField("scene_id")
    private Long sceneId;

    @TableField("scene_code")
    @DdlColumnLength(64)
    private String sceneCode;

    @TableField("version_tag")
    @DdlColumnLength(64)
    private String versionTag;

    @TableField("plan_id")
    private Long planId;

    @TableField("trigger_type")
    @DdlColumnLength(32)
    private String triggerType;

    @TableField("status")
    @DdlColumnLength(32)
    private String status;

    @TableField("success")
    private Integer success;

    @TableField("started_at")
    private LocalDateTime startedAt;

    @TableField("finished_at")
    private LocalDateTime finishedAt;

    @TableField("duration_ms")
    private Long durationMs;

    @TableField("request_snapshot")
    @Column(name = "request_snapshot", columnDefinition = "LONGTEXT")
    private String requestSnapshot;

    @TableField("result_summary")
    @Column(name = "result_summary", columnDefinition = "TEXT")
    private String resultSummary;
}
