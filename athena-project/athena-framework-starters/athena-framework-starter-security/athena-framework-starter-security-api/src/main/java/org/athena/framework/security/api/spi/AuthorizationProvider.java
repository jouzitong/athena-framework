package org.athena.framework.security.api.spi;

import java.util.Set;

/**
 * 授权数据提供者扩展点（SPI）。
 *
 * <p>职责：根据用户标识与租户标识提供“已展开”的权限集合，
 * 供认证后上下文构建、鉴权判定或权限快照生成使用。
 *
 * <p>典型数据来源：
 * <ul>
 *     <li>数据库中的用户-角色-权限关系表</li>
 *     <li>外部 IAM / SSO / 权限中心服务</li>
 *     <li>缓存层（如 Redis）与本地缓存组合</li>
 * </ul>
 *
 * <p>实现建议：
 * <ul>
 *     <li>无权限时返回空集合，避免返回 {@code null} 造成上游空指针处理复杂化。</li>
 *     <li>同一 userId 在不同 tenantId 下应独立计算，避免跨租户权限串扰。</li>
 *     <li>保证结果可重复、可预期；如涉及缓存，注意失效策略与一致性窗口。</li>
 * </ul>
 */
public interface AuthorizationProvider {

    /**
     * 查询指定用户在指定租户下的权限集合。
     *
     * <p>返回结果通常为权限码（如 {@code order:read}、{@code user:delete}），
     * 上游可据此进行接口、资源或数据级鉴权。
     *
     * @param userId   用户唯一标识
     * @param tenantId 租户标识；单租户场景可为空或固定值（取决于具体实现约定）
     * @return 权限集合；建议永不返回 {@code null}
     */
    Set<String> permissions(String userId, String tenantId);
}
