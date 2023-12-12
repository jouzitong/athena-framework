package org.arthena.framework.common.base;

import org.arthena.framework.common.annotation.GlobalEnum;

import java.io.Serializable;

/**
 * 所有的枚举类都应该实现该接口 与 {@link GlobalEnum} 配套使用
 *
 * @author zhouzhitong
 * @version 1.0
 * @since 2022/6/18
 */
@GlobalEnum
public interface BaseEnum extends Serializable {

    /**
     * 枚举 code
     *
     * @return code
     */
    Integer getCode();

    /**
     * 枚举描述
     *
     * @return 枚举描述
     */
    String getDesc();

}
