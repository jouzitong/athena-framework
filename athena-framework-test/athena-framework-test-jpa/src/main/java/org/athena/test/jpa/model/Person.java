package org.athena.test.jpa.model;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import org.athena.test.jpa.bean.Address;
import org.athena.test.jpa.type.Gender;

/**
 * @author zhouzhitong
 * @since 2025/7/13
 **/
@Data
@Table(name = "person")
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "name", length = 20, nullable = false, unique = true)
    private String name;

    private Integer age;

    private Gender gender;

    private Address address;

    private String test;

}
