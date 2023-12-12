package com.zhouzhitong.test.mybatis.type;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import org.arthena.common.base.BaseEnum;

/**
 * @author zhouzhitong
 * @since 2023-12-12
 **/

@Getter
public enum Gender implements BaseEnum {

    MAN(1, "男"),
    WOMAN(2, "女"),
    ;

    @JsonValue
    @EnumValue
    private final Integer code;

    private final String desc;

    Gender(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
