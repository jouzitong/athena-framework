package org.athena.framework.test.api.request;

import java.util.LinkedHashMap;
import java.util.Map;
import lombok.Data;
import org.athena.framework.test.api.enums.TestTriggerType;

/**
 * 测试执行请求。
 */
@Data
public class TestExecuteRequest {

    private Long sceneId;

    private String sceneCode;

    private String versionTag;

    private Long planId;

    private Long operatorId;

    private TestTriggerType triggerType = TestTriggerType.MANUAL;

    private Map<String, Object> variables = new LinkedHashMap<>();
}
