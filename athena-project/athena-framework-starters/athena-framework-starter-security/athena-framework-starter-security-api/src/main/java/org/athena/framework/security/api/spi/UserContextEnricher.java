package org.athena.framework.security.api.spi;

import org.athena.framework.security.api.model.MutableUserContext;

/**
 * 用户上下文增强器扩展点（SPI）。
 *
 * <p>用途：在“身份认证已完成”后，对 {@link MutableUserContext} 进行二次补全，
 * 例如追加角色、权限、组织信息、数据范围、标签属性等衍生上下文数据。
 *
 * <p>设计意图：
 * <ul>
 *     <li>将“认证”与“上下文扩展”解耦，便于按业务模块拆分实现。</li>
 *     <li>支持多个增强器按顺序串行执行，形成可组合的增强链。</li>
 *     <li>允许调用方按需装配，实现最小化依赖与渐进扩展。</li>
 * </ul>
 *
 * <p>实现建议：
 * <ul>
 *     <li>尽量保持幂等：同一上下文重复执行不应产生不一致结果。</li>
 *     <li>做好空值保护：在 subject 或授权信息缺失时优雅降级。</li>
 *     <li>只修改自己负责的数据域，避免无意覆盖其他增强器已写入内容。</li>
 * </ul>
 */
public interface UserContextEnricher {

    /**
     * 返回当前增强器在增强链中的执行顺序。
     *
     * <p>约定：数值越小越先执行；数值越大越后执行。
     * 通常应将“基础信息准备”放在前面，“依赖前置结果的聚合逻辑”放在后面。
     *
     * @return 执行顺序值
     */
    int order();

    /**
     * 执行上下文增强逻辑。
     *
     * <p>该方法通常由框架在认证流程内调用。实现方可读取 subject、authorization、attributes
     * 等已有信息，并补充本增强器负责的字段。
     *
     * @param context 可变用户上下文；实现方可在此对象上写入增强结果
     */
    void enrich(MutableUserContext context);
}
