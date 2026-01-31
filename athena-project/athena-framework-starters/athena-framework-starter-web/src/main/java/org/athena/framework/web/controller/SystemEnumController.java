package org.athena.framework.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.arthena.framework.common.annotation.GlobalEnum;
import org.arthena.framework.common.enums.IEnum;
import org.arthena.framework.common.utils.CamelCaseUtils;
import org.arthena.framework.common.utils.PackageUtil;
import org.athena.framework.web.dto.EnumDTO;
import org.athena.framework.web.properties.LibWebProperties;
import org.athena.framework.web.service.ICustomEnumsService;
import org.athena.framework.web.vo.IR;
import org.athena.framework.web.vo.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 获取系统所有枚举配置
 *
 * @author zhouzhitong
 * @since 2026/1/31
 */
@RestController
@RequestMapping("/common/v1/system/enums")
@Slf4j
public class SystemEnumController {

    @Autowired
    private LibWebProperties properties;

    @Autowired(required = false)
    private List<ICustomEnumsService> enumsServices;

    private volatile Map<String, List<EnumDTO>> map;

    @GetMapping
    public IR<Map<String, List<EnumDTO>>> getAllEnums() {
        if (this.map != null) {
            return R.ok(this.map);
        }
        List<Class<IEnum>> subClasses = PackageUtil.getSubClasses(IEnum.class, properties.getEnumPackages());
        Map<String, List<EnumDTO>> map = new HashMap<>();
        for (Class<IEnum> subClass : subClasses) {
            String simpleName = subClass.getSimpleName();
            GlobalEnum globalEnum = subClass.getAnnotation(GlobalEnum.class);
            String name = CamelCaseUtils.lowerFirst(simpleName);
            if (globalEnum != null && StringUtils.isNotBlank(globalEnum.name())) {
                name = globalEnum.name();
            }
            IEnum[] enumConstants = subClass.getEnumConstants();
            List<EnumDTO> enums = new ArrayList<>();
            for (IEnum enumConstant : enumConstants) {
                enums.add(EnumDTO.builder()
                        .code(enumConstant.getCode())
                        .name(enumConstant.getName())
                        .enable(enumConstant.isEnable())
                        .val(enumConstant)
                        .build());

            }
            map.put(name, enums);
        }

        if (CollectionUtils.isNotEmpty(enumsServices)) {
            for (ICustomEnumsService enumsService : enumsServices) {
                Map<String, List<EnumDTO>> enums = enumsService.getEnums();
                map.putAll(enums);
            }
        }
        this.map = map;
        LOGGER.info("build system enum: {}", map);
        return R.ok(map);
    }

    @PostMapping("/clearCache")
    public IR<Boolean> clearCache() {
        LOGGER.info("clear system enum cache");
        this.map = null;
        return R.ok(true);
    }

}
