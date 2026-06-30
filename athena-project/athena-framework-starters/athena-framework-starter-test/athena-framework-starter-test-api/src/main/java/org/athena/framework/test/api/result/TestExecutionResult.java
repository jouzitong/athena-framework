package org.athena.framework.test.api.result;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.athena.framework.test.api.enums.TestExecutionStatus;
import org.athena.framework.test.api.enums.TestTriggerType;

/**
 * 测试执行结果。
 */
@Data
public class TestExecutionResult {

    private Long executionId;

    private String executionNo;

    private Long sceneId;

    private String sceneCode;

    private String versionTag;

    private Long planId;

    private TestTriggerType triggerType;

    private TestExecutionStatus status;

    private Boolean success = Boolean.FALSE;

    private LocalDateTime startedAt;

    private LocalDateTime finishedAt;

    private Long durationMs;

    private String summary;

    private List<TestStepExecutionResult> stepResults = new ArrayList<>();
}
