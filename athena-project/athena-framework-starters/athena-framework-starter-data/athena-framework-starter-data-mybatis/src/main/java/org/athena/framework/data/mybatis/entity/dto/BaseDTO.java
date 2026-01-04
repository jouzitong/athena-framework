package org.athena.framework.data.mybatis.entity.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.arthena.framework.common.base.ExtensibleProperties;
import org.athena.framework.data.jdbc.entity.dto.IDTOV2;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serial;
import java.time.LocalDateTime;

/**
 * @author zhouzhitong
 * @since 2022/09/14
 */
@Setter
@Getter
@ToString
public class BaseDTO extends ExtensibleProperties implements IDTOV2<Long> {

    @Serial
    private static final long serialVersionUID = 6328681439535050530L;

    /**
     * 主键 id（唯一标识）
     */
    private Long id;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    /**
     * 创建者
     */
    protected Long createdBy;

    /**
     * 修改者
     */
    protected Long lastModifiedBy;

    /**
     * 版本
     */
    private Long version;

}
