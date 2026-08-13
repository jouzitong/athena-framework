# Security development

## Compose modules

Use security core with a concrete token implementation. In the snapshot, `athena.security.token.type` defaults to `jwt`, so JWT mode requires `athena-framework-starter-security-token-jwt` directly or transitively.

Use exactly one user store when needed:

- `athena-framework-starter-security-user-jpa` with `athena.security.user.jpa.enabled=true`.
- `athena-framework-starter-security-user-mybatis` with `athena.security.user.mybatis.enabled=true`.

The framework startup validator rejects both being enabled. Add `athena-framework-starter-security-authorization` and set `athena.security.authorization.enabled=true` for annotation-based authorization.

## Token modes

- `jwt`: requires the JWT token starter and `athena.security.token.jwt.enabled=true`.
- `local`: uses the auth-core local token manager and is appropriate only for bounded single-process/local scenarios.
- `redis`: properties exist, but this snapshot contains no Redis TokenManager artifact. Treat it as unavailable.

Externalize `athena.security.token.jwt.secret`. The source fallback is a development placeholder and must not reach a real environment.

## Authentication chain

Auth core supplies defaults guarded by missing-bean conditions, including credential extraction, identity/authentication services, filters, context integration, and a local token manager for local mode.

Replace `CredentialVerifier` in production. The default `PlainCredentialVerifier` compares the stored value directly to the supplied password and is explicitly development/test behavior.

Relevant public SPIs include:

- `SecurityUserRepository` and `IdentityProvider`.
- `CredentialVerifier` and `Authenticator`.
- `TokenManager` (`parseV2` is the non-deprecated parse path).
- `UserContextEnricher` for ordered, idempotent context enrichment.
- `AuthorizationProvider`, `PermissionEvaluator`, and `RoleProvider`.
- `AuditPublisher` and menu providers.

Prefer a consumer adapter that implements these contracts over replacing filters/controllers wholesale.

## Authorization

When authorization is enabled without a consumer provider, the snapshot can create an empty provider. That is safe-deny behavior, not a working permission source.

- Use stable permission codes such as `order:read`.
- Keep tenant ID in every permission lookup where multi-tenancy exists.
- Return empty sets instead of null.
- Define cache invalidation and acceptable consistency windows.
- Test denied, allowed, missing-context, and cross-tenant cases.

## Request enforcement

`athena.security.auth.require-token=true` installs the default required-token interceptor. Configure a narrowly reviewed `ignore-urls` list. Do not use broad patterns that expose business endpoints.

Gateway/header propagation must not be treated as proof of identity by downstream services unless headers are authenticated, stripped at the edge, and protected from direct external calls.

## Minimum production checks

1. Hash-based credential verifier.
2. Externalized, sufficiently strong, rotated JWT secret.
3. Token expiration and invalidation behavior.
4. Exactly one active user store.
5. Authorization module plus real permission provider when permissions are required.
6. Login rate limiting, audit, and sensitive logging controls.
7. Tests for invalid/expired/tampered tokens and missing credentials.
8. No default or literal secrets in configuration.
