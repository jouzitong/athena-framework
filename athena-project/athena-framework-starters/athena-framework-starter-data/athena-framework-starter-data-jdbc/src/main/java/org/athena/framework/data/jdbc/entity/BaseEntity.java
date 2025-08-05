package org.athena.framework.data.jdbc.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.time.LocalDateTime;

/**
 * 基Entity类
 * <pre>
 * 1、基于数据库的自增主键
 * 2、自动记录创建时间、修改时间、创建者、修改者
 * 3、软删除
 * </pre>
 *
 * @author zhouzhitong
 * @since 2022-09-07
 */
@Getter
@Setter
public class BaseEntity implements IEntity<Long> {

    @Serial
    private static final long serialVersionUID = 8328293151203544834L;

    /**
     * 主键
     */
    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "'主键ID'")
    protected Long id;

    /**
     * 记录创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "create_time", updatable = false, nullable = false, columnDefinition = "'创建时间' default CURRENT_TIMESTAMP")
    protected LocalDateTime createTime;

    /**
     * 记录修改时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "update_time", nullable = false, columnDefinition = "'修改时间' default CURRENT_TIMESTAMP")
    protected LocalDateTime updateTime;

    /**
     * 创建者
     */
    @Column(name = "created_by", nullable = false, columnDefinition = "'创建者' default 0")
    protected Long createdBy = 0L;

    /**
     * 修改者
     */
    @Column(name = "last_modified_by", nullable = false, columnDefinition = "'修改者' default 0")
    protected Long lastModifiedBy = 0L;

    /**
     * 版本
     */
//    @Version
    @Column(name = "version", nullable = false, columnDefinition = "'版本' default 1")
    protected Long version = 1L;

    /**
     * 软删除 0-未删除，1-已删除
     */
    @Column(name = "deleted", nullable = false, columnDefinition = "'软删除 0-未删除，1-已删除' default 0")
    protected Integer deleted = 0;

    public Long getAndIncrementVersion() {
//        return ++version;
        return -1L;
    }

}

