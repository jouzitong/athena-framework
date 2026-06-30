package org.athena.framework.test.api.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;
import org.athena.framework.test.api.enums.TestSceneStatus;

/**
 * 测试流程定义。
 */
@Data
public class TestSceneDefinition {

    private Long sceneId;

    private String sceneCode;

    private String name;

    private String bizType;

    private String description;

    private String versionTag;

    private TestSceneStatus status;

    private Map<String, String> variables = new LinkedHashMap<>();

    private List<TestStepDefinition> steps = new ArrayList<>();
}
