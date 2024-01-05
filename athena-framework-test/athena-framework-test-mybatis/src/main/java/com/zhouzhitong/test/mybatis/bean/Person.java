package com.zhouzhitong.test.mybatis.bean;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.zhouzhitong.test.mybatis.type.Gender;
import lombok.Data;
import org.athena.framework.mybatis.annotation.FieldComment;
import org.athena.framework.mybatis.entity.BaseEntity;
import org.athena.framework.mybatis.handler.DefaultJsonHandler;

/**
 * @author zhouzhitong
 * @since 2023-12-11
 **/
@Data
@TableName("person")
public class Person extends BaseEntity {

    @FieldComment("varchar(64) not null comment '姓名'")
    private String name;

    @FieldComment("int not null comment '年龄'")
    private Integer age;

    @FieldComment("varchar(11) not null comment '手机号'")
    private String phone;

    @FieldComment("int not null comment '性别'")
    private Gender gender;

    @FieldComment("json not null comment '地址'")
    @TableField(typeHandler = DefaultJsonHandler.class)
    private Address address;

}
