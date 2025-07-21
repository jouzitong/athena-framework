package org.athena.test.jpa.type;

/**
 * @author zhouzhitong
 * @since 2025/7/13
 **/
public enum Gender {

    UNKNOWN(0, "未知"),
    MALE(1, "男"),
    FEMALE(2, "女"),
    ;

    private final int code;

    private final String name;

    Gender(int code, String name) {
        this.code = code;
        this.name = name;
    }
}
