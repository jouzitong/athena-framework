package org.athena.framework.test.api.catalog.service;

import java.util.Optional;
import org.athena.framework.test.api.catalog.dto.ApiCatalogDTO;
import org.athena.framework.test.api.catalog.dto.ApiCatalogVersionDTO;
import org.athena.framework.test.api.catalog.request.ApiCatalogCreateRequest;
import org.athena.framework.test.api.catalog.request.ApiCatalogPublishRequest;

/**
 * 接口资产管理服务。
 */
public interface ApiCatalogService {

    ApiCatalogDTO create(ApiCatalogCreateRequest request);

    ApiCatalogVersionDTO publishVersion(ApiCatalogPublishRequest request);

    Optional<ApiCatalogDTO> getById(Long id);
}
