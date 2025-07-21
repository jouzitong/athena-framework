package org.athena.framework.data.mybatis.autoGen;

import org.athena.framework.data.mybatis.entity.BaseEntity;

import java.sql.Statement;

/**
 * DDL 数据结构生成服务
 *
 * @author zhouzhitong
 * @since 2025/7/7
 **/
public interface IDdlGenService {

    /**
     * 判断表是否存在
     *
     * @param tableName 表名
     * @param statement 数据库操作句柄
     * @return true 表示存在
     */
    boolean isExistTable(String tableName, Statement statement);

    /**
     * 获取创建表DDL
     *
     * @param target 目标实体
     * @return 创建表DDL
     */
    String getCreateTableDdl(Class<? extends BaseEntity> target);

    /**
     * 获取更新表DDL
     *
     * @param target    目标实体
     * @param statement 数据库操作句柄
     * @return 更新表DDL
     */
    String getUpdateTableDdl(Class<? extends BaseEntity> target, Statement statement);

}
