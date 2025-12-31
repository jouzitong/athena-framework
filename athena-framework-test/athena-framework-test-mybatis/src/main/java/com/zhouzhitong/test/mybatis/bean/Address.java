package com.zhouzhitong.test.mybatis.bean;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.arthena.framework.common.base.DBJson;

/**
 * @author zhouzhitong
 * @since 2023-12-11
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Address implements DBJson {

    @Column(name = "province", columnDefinition = "'省/洲'")
    @TableField( fill = FieldFill.INSERT_UPDATE)
    private String province;

    @Column(name = "city", columnDefinition = "'市'")
    @TableField( fill = FieldFill.INSERT_UPDATE)
    private String city;

    @Column(name = "district", columnDefinition = "'区/县'")
    @TableField( fill = FieldFill.INSERT_UPDATE)
    private String district;

}
