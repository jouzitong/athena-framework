package org.athena.framework.web.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author zhouzhitong
 * @since 2024-01-26
 **/
@Data
@NoArgsConstructor
public class ErrorParamVO implements Serializable {

    @Serial
    private final static long serialVersionUID = -1123123L;

    private String code;

    private String name;

    private String message;

}
