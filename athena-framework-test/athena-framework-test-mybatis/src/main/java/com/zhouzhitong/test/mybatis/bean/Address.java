package com.zhouzhitong.test.mybatis.bean;

import jakarta.persistence.Column;
import lombok.Data;
import org.arthena.framework.common.base.DBJson;

/**
 * @author zhouzhitong
 * @since 2023-12-11
 **/
@Data
public class Address implements DBJson {

    @Column(name = "province", columnDefinition = "'省/洲'")
    private String province;

    @Column(name = "city", columnDefinition = "'市'")
    private String city;

    @Column(name = "district", columnDefinition = "'区/县'")
    private String district;

}
