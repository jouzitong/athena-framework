package org.athena.framework.data.jdbc.entity.test;

import jakarta.persistence.Column;
import jakarta.persistence.Table;
import org.athena.framework.data.jdbc.entity.BaseEntity;

/**
 * @author zhouzhitong
 * @since 2025/7/17
 **/
@Table(name = "test_person")
public class TestPerson extends BaseEntity {

    @Column(name = "name", nullable = false, columnDefinition = "'name'")
    private String name;

    @Column(name = "age", nullable = false, columnDefinition = "'age'")
    private Integer age;

    @Column(name = "sex", nullable = false, columnDefinition = "'sex'")
    private Sex sex;

    private String address;

    private String phone;

}
