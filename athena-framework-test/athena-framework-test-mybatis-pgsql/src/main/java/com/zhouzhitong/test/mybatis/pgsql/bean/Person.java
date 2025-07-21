package com.zhouzhitong.test.mybatis.pgsql.bean;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.zhouzhitong.test.mybatis.pgsql.type.Gender;
import lombok.Data;
import org.athena.framework.data.jdbc.annotation.FieldComment;
import org.athena.framework.data.mybatis.entity.BaseEntity;
import org.athena.framework.data.mybatis.handler.DefaultJsonHandler;
import org.springframework.stereotype.Indexed;

/**
 * @author zhouzhitong
 * @since 2023-12-11
 **/
@Data
@TableName("person")
public class Person extends BaseEntity {

    @FieldComment(type = "varchar(64)", isAllowNull = false, comment = "姓名")
    private String name;

    @FieldComment(type = "int", isAllowNull = false, comment = "年龄")
    private Integer age;

//    @FieldComment(type = "varchar(11)", isAllowNull = false, comment = "手机号")
//    private String phone;

    @FieldComment(type = "int", isAllowNull = false, comment = "性别")
    private Gender gender;

    @FieldComment(type = "json", isAllowNull = false, comment = "地址")
    @TableField(typeHandler = DefaultJsonHandler.class)
    private Address address;

    @FieldComment(type = "varchar(64)", isAllowNull = false, comment = "测试")
    private String test;

    @FieldComment(type = "varchar(64)", isAllowNull = false, comment = "测试A")
    private String testA;

    @FieldComment(type = "varchar(64)", isAllowNull = false, comment = "测试B")
    private String testB;

}
