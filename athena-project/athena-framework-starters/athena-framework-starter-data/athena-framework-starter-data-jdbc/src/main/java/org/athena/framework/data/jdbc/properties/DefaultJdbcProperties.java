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
    private boolean enableEvent = false;

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
     *
     * @deprecated 兼容旧配置使用。下一个大版本移除，请改用 autoAddColumn、autoUpdateColumn、autoDropColumn。
     */
    @Deprecated(since = "1.4.2", forRemoval = true)
    private boolean autoUpdateTable = false;

    /**
     * 是否自动添加实体新增字段。
     */
    private boolean autoAddColumn = true;

    /**
     * 是否自动更新实体字段定义。
     * 开启后会根据实体定义生成 MySQL {@code MODIFY COLUMN} 语句。
     * 该操作可能影响存量数据，生产环境请先审核生成的 DDL。
     */
    private boolean autoUpdateColumn = false;

    /**
     * 是否自动删除实体已移除字段。
     */
    private boolean autoDropColumn = false;

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
