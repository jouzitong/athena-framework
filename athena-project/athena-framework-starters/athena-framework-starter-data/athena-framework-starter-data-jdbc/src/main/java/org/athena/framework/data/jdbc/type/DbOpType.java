package org.athena.framework.data.jdbc.type;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import org.arthena.framework.common.enums.IEnum;

/**
 *
 * @author zhouzhitong
 * @since 2026/1/15
 */
@Getter
public enum DbOpType implements IEnum {

    INSERT(1, "INSERT"),
    UPDATE(2, "UPDATE"),
    DELETE(3, "DELETE"),
    SELECT(4, "SELECT"),
    ;


    @JsonValue
    private final int code;

    private final String name;

    DbOpType(int code, String name) {
        this.code = code;
        this.name = name;
    }
}
