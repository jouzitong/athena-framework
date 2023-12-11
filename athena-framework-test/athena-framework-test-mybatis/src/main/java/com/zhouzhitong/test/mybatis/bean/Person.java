package com.zhouzhitong.test.mybatis.bean;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.zhouzhitong.lib.mapper.anno.FieldComment;
import com.zhouzhitong.lib.mapper.entity.BaseEntity;
import com.zhouzhitong.lib.mapper.handler.DefaultJsonHandler;
import lombok.Data;

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

    @FieldComment("json not null comment '地址'")
    @TableField(typeHandler = DefaultJsonHandler.class)
    private Address address;

}
