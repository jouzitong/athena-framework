package org.athena.framework.test.storage.mybatis.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.persistence.Column;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.athena.framework.data.mybatis.annotations.DdlColumnLength;
import org.athena.framework.data.mybatis.entity.LogicalDeleteEntity;

/**
 * 接口资产版本实体。
 */
@Getter
@Setter
@TableName("athena_test_api_catalog_version")
public class ApiCatalogVersionEntity extends LogicalDeleteEntity {

    @TableField("api_catalog_id")
    private Long apiCatalogId;

    @TableField("version_tag")
    @DdlColumnLength(64)
    private String versionTag;

    @TableField("version_status")
    @DdlColumnLength(32)
    private String versionStatus;

    @TableField("definition_json")
    @Column(name = "definition_json", columnDefinition = "LONGTEXT")
    private String definitionJson;

    @TableField("change_comment")
    @Column(name = "change_comment", columnDefinition = "TEXT")
    private String changeComment;

    @TableField("published_by")
    private Long publishedBy;

    @TableField("published_at")
    private LocalDateTime publishedAt;
}
