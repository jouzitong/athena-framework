package org.athena.framework.web.service;

import org.athena.framework.web.dto.EnumDTO;
import org.springframework.core.Ordered;

import java.util.List;
import java.util.Map;

/**
 * 自定义枚举类型提供服务
 *
 * @author zhouzhitong
 * @since 2026/1/31
 */
public interface ICustomEnumsService extends Ordered {

    /**
     * Returns a map where each key is a string representing the name of an enum type, and the value is a list of {@link EnumDTO} objects.
     * Each {@link EnumDTO} in the list represents a specific constant of the enum type, containing details such as the code, display name, and whether it is enabled.
     *
     * @return a map with enum type names as keys and lists of their corresponding {@link EnumDTO} instances as values
     */
    Map<String, List<EnumDTO>> getEnums();

    @Override
    default int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
