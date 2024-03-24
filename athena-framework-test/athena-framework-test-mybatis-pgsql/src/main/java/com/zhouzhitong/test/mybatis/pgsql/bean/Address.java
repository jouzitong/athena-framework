package com.zhouzhitong.test.mybatis.pgsql.bean;

import lombok.Data;
import org.arthena.framework.common.base.DBJson;

/**
 * @author zhouzhitong
 * @since 2023-12-11
 **/
@Data
public class Address implements DBJson {

    private String province;

    private String city;

    private String district;

}
