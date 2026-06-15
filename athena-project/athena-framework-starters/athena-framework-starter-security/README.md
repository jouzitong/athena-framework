# Athena Framework Security Starter

## Dependency Contract

- `athena-framework-starter-security`
  - Core security foundation
  - Provides token extraction, request context, and base security properties
  - Does not pull in user persistence or RBAC modules

- `athena-framework-starter-security-gateway`
  - Recommended dependency for gateway and read-only services
  - Brings `security` plus JWT token support
  - Default mode is `jwt`

- `athena-framework-starter-security-user-jpa`
  - Add when the service needs user lookup, credential verification, and JPA-based RBAC/enrichment

- `athena-framework-starter-security-user-mybatis`
  - Add when the service needs user lookup, credential verification, and MyBatis-based RBAC/enrichment

- `athena-framework-starter-security-authorization`
  - Add only when annotation-based permission checks are required

## Token Modes

- Default: `athena.security.token.type=jwt`
- Explicit fallback: `athena.security.token.type=local`
- Planned future extension: Redis-backed JWT session/login management

## Suggested Usage

- Gateway / read-only service:
  - `athena-framework-starter-security-gateway`

- Login / user service:
  - `athena-framework-starter-security`
  - `athena-framework-starter-security-token-jwt`
  - `athena-framework-starter-security-user-jpa` or `athena-framework-starter-security-user-mybatis`

