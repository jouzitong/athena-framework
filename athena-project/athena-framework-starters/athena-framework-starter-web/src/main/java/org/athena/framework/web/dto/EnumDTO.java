package org.athena.framework.web.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.arthena.framework.common.enums.IEnum;

/**
 * 全局枚举DTO
 *
 * @author zhouzhitong
 * @since 2024/11/27
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnumDTO {

    /**
     * 确定传的参数
     */
    private Object code;

    /**
     * 枚举展示名称
     */
    private String name;

    private Boolean enable;

    /**
     * 具体枚举对象
     */
    @JsonIgnore
    @Deprecated
    private IEnum val;

    // 主要是应对前端，常用的名称说明

    public Object getValue(){
        return code;
    }

    public String getLabel(){
        return name;
    }

}
