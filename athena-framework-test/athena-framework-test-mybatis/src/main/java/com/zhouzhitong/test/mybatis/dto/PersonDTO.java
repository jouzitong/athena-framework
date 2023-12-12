package com.zhouzhitong.test.mybatis.dto;

import com.baomidou.mybatisplus.annotation.TableName;
import com.zhouzhitong.test.mybatis.bean.Address;
import lombok.Data;
import org.athena.framework.mybatis.dto.BaseDTO;

/**
 * @author zhouzhitong
 * @since 2023-12-11
 **/
@Data
@TableName("person")
public class PersonDTO extends BaseDTO {

    private String name;

    private Integer age;

    private String phone;

    private Address address;

}
