package com.zhouzhitong.test.mybatis.pgsql.type;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import org.arthena.framework.common.enums.IEnum;

/**
 * @author zhouzhitong
 * @since 2023-12-12
 **/

@Getter
public enum Gender implements IEnum {

    MAN(1, "男"),
    WOMAN(2, "女"),
    ;

    @JsonValue
    @EnumValue
    private final int code;

    private final String name;

    Gender(int code, String name) {
        this.code = code;
        this.name = name;
    }
}
