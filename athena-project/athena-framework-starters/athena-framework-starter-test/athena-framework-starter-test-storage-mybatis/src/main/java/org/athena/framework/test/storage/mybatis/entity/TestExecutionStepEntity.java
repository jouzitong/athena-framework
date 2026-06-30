package org.athena.framework.test.storage.mybatis.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;
import org.athena.framework.data.mybatis.annotations.DdlColumnLength;
import org.athena.framework.data.mybatis.entity.AuditableEntity;

/**
 * 测试执行步骤记录实体。
 */
@Getter
@Setter
@TableName("athena_test_execution_step")
public class TestExecutionStepEntity extends AuditableEntity {

    @TableField("execution_id")
    private Long executionId;

    @TableField("step_code")
    @DdlColumnLength(64)
    private String stepCode;

    @TableField("step_name")
    @DdlColumnLength(128)
    private String stepName;

    @TableField("step_order")
    private Integer stepOrder;

    @TableField("step_type")
    @DdlColumnLength(32)
    private String stepType;

    @TableField("status")
    @DdlColumnLength(32)
    private String status;

    @TableField("success")
    private Integer success;

    @TableField("duration_ms")
    private Long durationMs;

    @TableField("request_payload")
    @Column(name = "request_payload", columnDefinition = "LONGTEXT")
    private String requestPayload;

    @TableField("response_payload")
    @Column(name = "response_payload", columnDefinition = "LONGTEXT")
    private String responsePayload;

    @TableField("error_message")
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @TableField("extracted_variables")
    @Column(name = "extracted_variables", columnDefinition = "LONGTEXT")
    private String extractedVariables;
}
