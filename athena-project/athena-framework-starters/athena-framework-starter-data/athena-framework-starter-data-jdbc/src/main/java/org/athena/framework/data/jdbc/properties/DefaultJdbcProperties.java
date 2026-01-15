package org.athena.framework.data.jdbc.properties;

import lombok.Data;
import org.athena.framework.data.jdbc.type.DbType;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author zhouzhitong
 * @since 2025/7/13
 **/
@Component
@ConfigurationProperties(prefix = "lib.jdbc")
@Data
public class DefaultJdbcProperties {

    /**
     * 标识是否启用事件功能。
     * 当设置为 true 时，表示启用事件；设置为 false 时，则禁用事件。
     */
    private boolean enableEvent;

    /**
     * 是否开启创建表的ddl
     */
    private boolean enableCreateTableDdl = true;

    /**
     * 指定数据类型: 目前支持 mysql, pgsql
     */
    private DbType type = DbType.MYSQL;

    /**
     * 创建表 ddl 的文件目录
     */
    private String tableDDLPathFile = "config";

    private Resource tableDDLPathFile2 = new ClassPathResource("config/");

    /**
     * 是否开启自动更新表
     * <p>
     * 生产环境不建议开启
     */
    private boolean autoUpdateTable = false;

    /**
     * 实体扫描包
     */
    private List<String> baseEntityPackages = List.of("org.athena.framework");

    /**
     * 忽略表前缀
     */
    private String ignorePrefix;

    /**
     * 忽略表后缀
     */
    private String ignoreSubfix;

}
