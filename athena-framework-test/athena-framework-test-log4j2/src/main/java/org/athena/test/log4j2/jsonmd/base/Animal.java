package org.athena.test.log4j2.jsonmd.base;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

/**
 *
 * @author zhouzhitong
 * @since 2026/1/12
 */
@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
public abstract class Animal {

    private String name;

}
