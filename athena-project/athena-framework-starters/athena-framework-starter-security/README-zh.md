# Athena Framework Security Starter

## 依赖契约

- `athena-framework-starter-security`
  - 安全能力的基础层
  - 提供 token 提取、请求上下文和基础安全属性
  - 不会引入用户持久化或 RBAC 模块

- `athena-framework-starter-security-gateway`
  - 推荐给 gateway 和只读服务使用
  - 在 `security` 基础上增加 JWT token 支持
  - 默认模式为 `jwt`

- `athena-framework-starter-security-user-jpa`
  - 当服务需要用户查询、凭据校验，以及基于 JPA 的 RBAC/上下文增强时引入

- `athena-framework-starter-security-user-mybatis`
  - 当服务需要用户查询、凭据校验，以及基于 MyBatis 的 RBAC/上下文增强时引入

- `athena-framework-starter-security-authorization`
  - 仅在需要基于注解的权限校验时引入

## Token 模式

- 默认值：`athena.security.token.type=jwt`
- 显式降级：`athena.security.token.type=local`
- 未来扩展：基于 Redis 的 JWT 会话/登录管理

## 推荐用法

- Gateway / 只读服务：
  - `athena-framework-starter-security-gateway`

- 登录 / 用户服务：
  - `athena-framework-starter-security`
  - `athena-framework-starter-security-token-jwt`
  - `athena-framework-starter-security-user-jpa` 或 `athena-framework-starter-security-user-mybatis`

