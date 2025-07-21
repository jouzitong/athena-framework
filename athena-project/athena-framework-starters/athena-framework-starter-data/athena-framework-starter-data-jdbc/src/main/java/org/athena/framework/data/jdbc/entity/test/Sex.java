package org.athena.framework.data.jdbc.entity.test;

import lombok.Getter;
import org.arthena.framework.common.enums.IEnum;

/**
 * @author zhouzhitong
 * @since 2025/7/17
 **/
@Getter
public enum Sex implements IEnum {

    UNKNOWN(0, "未知"),
    MALE(1, "男"),
    WOMEN(2, "女"),

    ;
    private final int code;

    private final String name;

    Sex(int code, String name) {
        this.code = code;
        this.name = name;
    }
}
