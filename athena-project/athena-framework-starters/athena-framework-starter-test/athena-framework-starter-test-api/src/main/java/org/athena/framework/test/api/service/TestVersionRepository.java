package org.athena.framework.test.api.service;

import java.util.Optional;
import org.athena.framework.test.api.model.TestSceneDefinition;

/**
 * 测试版本读取接口。
 */
public interface TestVersionRepository {

    Optional<TestSceneDefinition> findSceneVersion(Long sceneId, String versionTag);
}
