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
//@Entity
//@Table(name = Agv.TABLE,
//        uniqueConstraints = {
//                @UniqueConstraint(name = "uniq_agvId", columnNames = {"agv_id"})
//        })
//@org.hibernate.annotations.Table(appliesTo = Agv.TABLE, comment = "agv 表")
//@EqualsAndHashCode(callSuper = false)
//@Accessors
//@Where(clause = "deleted=0")
//@SQLDelete(sql = "update " + Agv.TABLE + " set deleted = 1 where id = ?")
//@TypeDef(name = "json", typeClass = JsonStringType.class)
//@Data
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
