package org.authena.mybatis.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author zhouzhitong
 * @since 2023-11-14
 **/
@Data
@Component
@ConfigurationProperties("lib.mapper.auto-gen")
@NoArgsConstructor
public class DefaultMapperProperties {


    /**
     * 是否开启创建表的ddl
     */
    private boolean enableCreateTableDdl = true;

    /**
     * 指定数据类型: 目前支持 mysql
     */
    private String type;

    /**
     * 创建表 ddl 的文件目录
     */
    private String tableDDLPathFile = "config/dd.sql";

    /**
     * 是否开启自动更新表
     * <p>
     * 生产环境不建议开启
     */
    private boolean autoUpdateTable = false;

    /**
     * 实体扫描包
     */
    private List<String> baseEntityPackages = List.of("com.zhouzhitong");

    /**
     * 实体字段类型与数据库字段类型映射关系
     */
    private List<FieldTypeMap> fieldTypeMaps;

    public List<FieldTypeMap> getFieldTypeMaps() {
        return fieldTypeMaps == null ? List.of() : fieldTypeMaps;
    }
}
