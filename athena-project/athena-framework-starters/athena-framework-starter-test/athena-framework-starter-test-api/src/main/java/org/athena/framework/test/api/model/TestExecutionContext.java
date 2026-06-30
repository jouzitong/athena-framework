package org.athena.framework.test.api.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;
import org.athena.framework.test.api.enums.TestTriggerType;
import org.athena.framework.test.api.result.TestStepExecutionResult;

/**
 * 单次测试执行的运行态上下文。
 */
@Data
public class TestExecutionContext {

    private Long sceneId;

    private String sceneCode;

    private String versionTag;

    private TestTriggerType triggerType;

    private Long operatorId;

    private LocalDateTime startedAt;

    private Map<String, Object> variables = new LinkedHashMap<>();

    private List<TestStepExecutionResult> stepResults = new ArrayList<>();
}
