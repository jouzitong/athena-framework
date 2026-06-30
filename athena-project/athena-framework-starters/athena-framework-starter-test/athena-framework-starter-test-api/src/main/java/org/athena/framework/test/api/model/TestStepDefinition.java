package org.athena.framework.test.api.model;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.athena.framework.test.api.enums.TestStepType;

/**
 * 测试步骤定义。
 */
@Data
public class TestStepDefinition {

    private String stepCode;

    private String name;

    private Integer stepOrder;

    private TestStepType stepType;

    /**
     * 协议类型，便于同一执行器后续支持 http/ws 子类型扩展。
     */
    private String protocol;

    /**
     * 结构化配置快照，建议存储 json。
     */
    private String configJson;

    private Long timeoutMs = 30000L;

    private Integer retryTimes = 0;

    private Boolean continueOnFailure = Boolean.FALSE;

    private List<TestAssertionDefinition> assertions = new ArrayList<>();

    private List<TestExtractorDefinition> extractors = new ArrayList<>();
}
