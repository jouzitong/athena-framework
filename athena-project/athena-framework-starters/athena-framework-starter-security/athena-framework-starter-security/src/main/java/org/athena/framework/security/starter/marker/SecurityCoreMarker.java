package org.athena.framework.security.starter.marker;

/**
 * 安全核心模块标记接口。
 * 供其它可选模块通过 {@code @ConditionalOnBean} 判断核心能力是否已启用。
 */
public interface SecurityCoreMarker {
}
