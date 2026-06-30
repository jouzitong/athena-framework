package org.athena.framework.test.storage.mybatis.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;
import org.athena.framework.data.mybatis.annotations.DdlColumnLength;
import org.athena.framework.data.mybatis.entity.LogicalDeleteEntity;

/**
 * 测试场景定义实体。
 */
@Getter
@Setter
@TableName("athena_test_scene")
public class TestSceneEntity extends LogicalDeleteEntity {

    @TableField("scene_code")
    @DdlColumnLength(64)
    private String sceneCode;

    @TableField("name")
    @DdlColumnLength(128)
    private String name;

    @TableField("biz_type")
    @DdlColumnLength(64)
    private String bizType;

    @TableField("description")
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @TableField("current_version")
    @DdlColumnLength(64)
    private String currentVersion;

    @TableField("status")
    @DdlColumnLength(32)
    private String status;
}
