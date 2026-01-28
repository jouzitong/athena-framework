package org.arthena.framework.common.enums;

import org.arthena.framework.common.annotation.EnumValue;
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
public interface IEnum extends Serializable {

    /**
     * 枚举 code
     *
     * @return code
     */
    @EnumValue
    int getCode();

    /**
     * 枚举描述
     *
     * @return 枚举描述
     */
    String getName();

    /**
     * 当前枚举值是否可用
     *
     * @return 是否可用
     */
    default boolean isEnable() {
        return true;
    }

    default String toStr() {
        return getName();
    }

}
