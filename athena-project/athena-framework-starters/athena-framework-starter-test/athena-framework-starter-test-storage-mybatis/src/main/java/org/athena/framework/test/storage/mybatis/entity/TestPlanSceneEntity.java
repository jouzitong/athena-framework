package org.athena.framework.test.storage.mybatis.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import org.athena.framework.data.mybatis.annotations.DdlColumnLength;
import org.athena.framework.data.mybatis.entity.LogicalDeleteEntity;

/**
 * 测试计划和场景关系实体。
 */
@Getter
@Setter
@TableName("athena_test_plan_scene")
public class TestPlanSceneEntity extends LogicalDeleteEntity {

    @TableField("plan_id")
    private Long planId;

    @TableField("scene_id")
    private Long sceneId;

    @TableField("version_tag")
    @DdlColumnLength(64)
    private String versionTag;

    @TableField("execute_order")
    private Integer executeOrder;

    @TableField("enabled")
    private Integer enabled = 1;
}
