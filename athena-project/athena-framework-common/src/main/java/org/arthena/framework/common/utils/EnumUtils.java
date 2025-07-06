package org.arthena.framework.common.utils;

import org.arthena.framework.common.base.IEnum;
import org.arthena.framework.common.exception.EnumNotFindException;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * 枚举工具类
 *
 * @author zhouzhitong
 * @since 2023/2/19
 */
@Slf4j
public class EnumUtils {

    public static <T extends IEnum> T codeOf(Class<T> c, Integer code) {
        if (code == null) {
            return null;
        }
        T[] enumConstants = c.getEnumConstants();
        for (T e : enumConstants) {
            if (Objects.equals(e.getCode(), code)) {
                return e;
            }
        }
        LOGGER.error("enum type {} don't have code {}.", c, code);
        throw EnumNotFindException.instant(c, code);
    }

}
