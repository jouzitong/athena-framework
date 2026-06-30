package org.athena.framework.test.api.catalog.service;

import java.util.Optional;
import org.athena.framework.test.api.catalog.dto.ApiCatalogDefinitionDTO;

/**
 * 接口资产引用解析服务。
 */
public interface ApiCatalogReferenceService {

    Optional<ApiCatalogDefinitionDTO> resolveByCatalog(Long apiCatalogId, String versionTag);
}
