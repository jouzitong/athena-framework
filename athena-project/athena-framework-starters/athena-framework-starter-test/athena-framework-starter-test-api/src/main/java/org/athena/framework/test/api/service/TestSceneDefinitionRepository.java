package org.athena.framework.test.api.service;

import java.util.Optional;
import org.athena.framework.test.api.model.TestSceneDefinition;

/**
 * 测试流程定义读取接口。
 */
public interface TestSceneDefinitionRepository {

    Optional<TestSceneDefinition> findBySceneIdAndVersion(Long sceneId, String versionTag);

    Optional<TestSceneDefinition> findBySceneCodeAndVersion(String sceneCode, String versionTag);
}
