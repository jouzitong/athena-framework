package org.athena.test.log4j2.jsonmd;

import lombok.Data;
import org.athena.test.log4j2.jsonmd.base.Animal;

/**
 *
 * @author zhouzhitong
 * @since 2026/1/12
 */
@Data
public class Cat extends Animal {

    private boolean isIndoor = true;

}
