---
name: athena-security
description: Design, implement, refactor, and verify authentication, JWT or local token handling, gateway trust, user persistence, RBAC, permission authorization, security context, audit, and security configuration for Java and Spring projects consuming Athena Framework. Use when a consumer project needs Athena security integration or an authorization model.
---

# Athena Security

Separate source ACLs, virtual authorization, and physical guardrails. Athena provides security contracts and extension points; the consumer owns identity mapping, user lifecycle, permissions, tenant policy, sensitive operations, and audit requirements.

## Required workflow

1. Read the consumer `AGENTS.md`, inspect its POM/configuration, and run the core recommender with `security` plus the chosen user store (`security-jpa` or `security-mybatis`).
2. Read the project-local `./.codex/skills/athena-framework-core/references/security.md`, `extension-points.md`, `architecture-and-boundaries.md`, and `data.md`. Inspect the exact JAR/source whenever the consumer version does not match the project-local manifest.
3. Select deliberately:
   - `athena-framework-starter-security` for the security core;
   - `athena-framework-starter-security-token-jwt` when `athena.security.token.type=jwt` (the default mode);
   - exactly one of `athena-framework-starter-security-user-jpa` and `athena-framework-starter-security-user-mybatis` for framework user/RBAC/audit persistence;
   - `athena-framework-starter-security-authorization` only when annotation authorization is explicitly enabled;
   - `athena-framework-starter-security-gateway` only for its gateway aggregation boundary.
4. Define authentication flow, token extraction/renewal/revocation, gateway trust, user-context enrichment, role/permission model, tenant isolation, public routes, failure responses, and audit events before writing beans.
5. Use the core `security-extension` scaffold for consumer-owned beans only after a dry-run review. Prefer public SPIs and `@ConditionalOnMissingBean` replacement points over framework forks.
6. Externalize JWT signing keys and all credentials. Reject development defaults in production. Never log raw tokens, passwords, signing keys, or gateway secrets.
7. Test unauthenticated, authenticated, forbidden, expired-token, malformed-token, cross-tenant, replay/idempotency, public-route, audit, and gateway-header cases. Verify the active user store is singular.
8. Run core static validation and bounded Maven tests. Report unverified identity providers or gateway services explicitly.

## Guardrails

- A Web starter does not provide authentication, and a JWT starter does not define the consumer's authorization model.
- Do not enable both JPA and MyBatis security user stores.
- Do not select `redis` token mode unless the exact resolved version provides a TokenManager; the bundled snapshot does not.
- Do not accept gateway headers without a verified trust boundary and network/secret guardrails.
- Do not call a security design complete until denial paths and audit behavior are tested.

Read the project-local `./.codex/skills/athena-framework-core/references/security.md` for property names and extension contracts, and `./.codex/skills/athena-framework-core/references/testing-and-acceptance.md` before claiming acceptance.
