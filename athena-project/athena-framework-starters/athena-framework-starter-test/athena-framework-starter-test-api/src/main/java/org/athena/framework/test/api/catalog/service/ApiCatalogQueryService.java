package org.athena.framework.test.api.catalog.service;

import java.util.List;
import org.athena.framework.test.api.catalog.dto.ApiCatalogDTO;
import org.athena.framework.test.api.catalog.request.ApiCatalogQueryRequest;

/**
 * 接口资产查询服务。
 */
public interface ApiCatalogQueryService {

    List<ApiCatalogDTO> search(ApiCatalogQueryRequest request);
}
