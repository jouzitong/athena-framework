package org.athena.framework.test.api.catalog.dto;

import java.time.LocalDateTime;
import lombok.Data;

/**
 * 接口资产版本快照。
 */
@Data
public class ApiCatalogVersionDTO {

    private Long id;

    private Long apiCatalogId;

    private String versionTag;

    private String versionStatus;

    private String changeComment;

    private LocalDateTime publishedAt;

    private Long publishedBy;
}
