package org.athena.framework.mybatis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.athena.framework.mybatis.anno.FieldComment;

import java.io.Serial;
import java.io.Serializable;
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
public class BaseEntity extends Model<BaseEntity> implements Serializable {

    @Serial
    private static final long serialVersionUID = 8328293151203544834L;

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    protected Long id;

    /**
     * 记录创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @FieldComment("datetime default CURRENT_TIMESTAMP not null comment '创建时间'")
    protected LocalDateTime createTime;

    /**
     * 记录修改时间
     */

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @FieldComment("datetime default CURRENT_TIMESTAMP not null comment '修改时间'")
    protected LocalDateTime updateTime;

    /**
     * 创建者
     */

    @FieldComment("bigint default 1' not null comment '创建者'")
    protected Long createdBy = 0L;

    /**
     * 修改者
     */
    @FieldComment("bigint default 1' not null comment '修改者'")
    protected Long lastModifiedBy =0L;

    /**
     * 版本
     */

//    @Version
//    @FieldComment("bigint default 0' not null comment '版本'")
//    protected Long version = 0L;

    /**
     * 软删除 0-未删除，1-已删除
     */
    @FieldComment("tinyint default 0' not null comment '软删除 0-未删除，1-已删除'")
    protected Integer deleted = 0;

    /**
     * 获取主键值
     *
     * @return 主键值
     */
    @Override
    public Serializable pkVal() {
        return this.id;
    }

    public long getAndIncrementVersion() {
//        return ++version;
        return -1L;
    }

}

