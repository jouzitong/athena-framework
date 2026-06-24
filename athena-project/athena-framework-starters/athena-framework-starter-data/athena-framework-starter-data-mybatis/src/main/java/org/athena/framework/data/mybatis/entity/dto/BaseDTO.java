package org.athena.framework.data.mybatis.entity.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.athena.framework.data.jdbc.entity.dto.IDTO;

import java.io.Serial;

/**
 * DTO 基类，仅承载主键和乐观锁版本号。
 *
 * @author zhouzhitong
 * @since 2022/09/14
 */
@Setter
@Getter
@ToString
public abstract class BaseDTO implements IDTO {

    @Serial
    private static final long serialVersionUID = 6328681439535050530L;

    /**
     * 主键 id（唯一标识）
     */
    private Long id;

    /**
     * 版本
     */
    private Long version;

}
