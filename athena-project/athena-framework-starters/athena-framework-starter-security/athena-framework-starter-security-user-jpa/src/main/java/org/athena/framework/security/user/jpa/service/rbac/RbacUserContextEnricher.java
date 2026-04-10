package org.athena.framework.security.user.jpa.service.rbac;

import org.athena.framework.security.api.model.AuthorizationSnapshot;
import org.athena.framework.security.api.model.MutableUserContext;
import org.athena.framework.security.api.spi.RolePermissionResolver;
import org.athena.framework.security.api.spi.RoleProvider;
import org.athena.framework.security.api.spi.UserContextEnricher;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 基于 RBAC（Role-Based Access Control，基于角色的访问控制）模型补全用户上下文的增强器。
 *
 * <p>主要职责：
 * <ul>
 *     <li>根据当前登录主体（subject）查询其角色集合。</li>
 *     <li>基于角色 + 用户信息解析其权限集合。</li>
 *     <li>在不丢失已有授权范围（scopes）的前提下，重建并回填授权快照。</li>
 *     <li>将角色与权限写入上下文属性，便于后续链路（审计、日志、扩展逻辑）使用。</li>
 * </ul>
 *
 * <p>该类只负责“上下文增强”，不负责持久化，也不直接做鉴权判定。
 */
public class RbacUserContextEnricher implements UserContextEnricher {

    /**
     * 角色提供器：负责按用户维度查询角色集合。
     */
    private final RoleProvider roleProvider;

    /**
     * 权限解析器：将角色映射/扩展为权限集合，可结合用户与租户做细粒度计算。
     */
    private final RolePermissionResolver rolePermissionResolver;

    /**
     * 构造增强器并注入 RBAC 解析所需依赖。
     *
     * @param roleProvider           角色来源
     * @param rolePermissionResolver 权限解析策略
     */
    public RbacUserContextEnricher(RoleProvider roleProvider,
                                   RolePermissionResolver rolePermissionResolver) {
        this.roleProvider = roleProvider;
        this.rolePermissionResolver = rolePermissionResolver;
    }

    /**
     * 指定增强器执行顺序。
     *
     * <p>约定：值越小越先执行。这里返回 20，意味着该增强器通常在基础身份信息初始化后执行，
     * 以便拿到完整 subject，再进行角色/权限补全。
     */
    @Override
    public int order() {
        return 20;
    }

    /**
     * 对用户上下文执行 RBAC 信息补全。
     *
     * <p>处理流程：
     * <ol>
     *     <li>若当前上下文没有主体信息，直接返回（无法定位用户，不做任何增强）。</li>
     *     <li>查询用户角色集合 roles。</li>
     *     <li>基于 roles + 用户/租户信息解析权限集合 permissions。</li>
     *     <li>保留已有 scopes（若不存在则使用空集合），避免覆盖上游已写入的授权范围。</li>
     *     <li>重建 AuthorizationSnapshot 并写回 context。</li>
     *     <li>额外把 roles / permissions 放入 attributes，提供给后续组件复用。</li>
     * </ol>
     *
     * @param context 可变用户上下文
     */
    @Override
    public void enrich(MutableUserContext context) {
        // 没有 subject 代表当前请求尚未完成身份识别，RBAC 计算无从进行，直接短路返回。
        if (context.subject() == null) {
            return;
        }

        // 按“用户ID + 租户ID”获取角色，兼容多租户场景下同一用户不同租户拥有不同角色。
        Set<String> roles = roleProvider.roles(context.subject().userId(), context.subject().tenantId());
        // 基于角色进一步解析权限；解析器可以按业务策略做角色继承、动态权限计算等。
        Set<String> permissions = rolePermissionResolver.permissions(roles, context.subject().userId(), context.subject().tenantId());
        // 复用已有 scopes：若授权信息或 scopes 为空，则回退为空集合，避免 NPE。
        // 使用 LinkedHashSet 保留原有迭代顺序，便于后续展示或调试时结果稳定。
        Set<String> scopes = context.authorization() == null || context.authorization().scopes() == null
            ? Set.of()
            : new LinkedHashSet<>(context.authorization().scopes());
        // 统一回写授权快照：权限、角色、范围三类信息在此形成当前请求的一致视图。
        context.setAuthorization(new AuthorizationSnapshot(permissions, roles, scopes));
        // 额外写入 attributes，给后续链路做透传和扩展读取（例如日志、审计、自定义拦截器）。
        context.attributes().put("roles", roles);
        context.attributes().put("permissions", permissions);
    }
}
