package org.athena.test.log4j2.jsonmd;

import lombok.Data;
import lombok.Setter;
import org.athena.test.log4j2.jsonmd.base.Animal;

/**
 *
 * @author zhouzhitong
 * @since 2026/1/12
 */
@Data
public class Dog extends Animal {

    private String breed = "1";

}
