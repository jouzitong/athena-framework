package com.zhouzhitong.test.mybatis.bean;

import com.baomidou.mybatisplus.annotation.TableName;
import com.zhouzhitong.test.mybatis.type.Gender;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import lombok.Data;
import org.athena.framework.data.mybatis.entity.BaseEntity;

/**
 * @author zhouzhitong
 * @since 2023-12-11
 **/
@Data
@TableName("person")
public class Person extends BaseEntity {

    @Column(name = "name",length = 64, nullable = false, columnDefinition = "'姓名'")
    private String name;

    @Column(name = "age", nullable = false, columnDefinition = "'年龄'")
    private Integer age;

//    @FieldComment("varchar(11) not null comment '手机号'")
//    private String phone;

    @Column(name = "gender", nullable = false, columnDefinition = "'性别'")
    private Gender gender;

    @Embedded
    private Address address;

}
