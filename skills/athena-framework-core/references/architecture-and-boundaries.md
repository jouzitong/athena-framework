# Architecture and boundaries

## Snapshot scope

This skill snapshot was generated from Athena Framework `1.4.2-SNAPSHOT` at the commit recorded in `framework-manifest.json`. The root build uses Java 17 and aggregates `athena-project` plus the BOM; it does not currently aggregate `athena-framework-test`.

Treat every claim as version-scoped. When a consumer resolves another Athena version, inspect that dependency's JAR, POM, `AutoConfiguration.imports`, configuration metadata/source, or a matching source checkout.

## Layer model

Use the framework in this direction:

```text
business application
  -> consumer adapters and business modules
    -> Athena public starters, APIs, and SPIs
      -> Spring Boot / Spring Cloud / persistence and infrastructure libraries
```

Athena is not a business application or a complete application generator. It supplies shared engineering capabilities and conventions through Maven dependencies and Spring Boot auto-configuration.

## Consumer responsibilities

Keep these concerns in the consuming project:

- Domain entities, commands, use cases, policies, and business error codes.
- Public API design, authorization decisions, validation, and compatibility promises.
- Database schema ownership and production migration review.
- Implementations of business-facing Athena SPIs, such as identity lookup, permissions, WebSocket ACL, and external messaging decisions.
- Environment-specific endpoints, credentials, secrets, timeouts, capacity, observability, and rollout policy.
- Integration and acceptance tests against the services actually used.

## Framework responsibilities

Use Athena for capabilities it already exposes:

- Common context, events, errors, utilities, thread context propagation, and local locks.
- Web response/error conventions and MVC infrastructure.
- Data abstractions plus MyBatis/JPA implementations.
- Optional security, cloud, communication, Kafka, WebSocket, MinIO, logging, and testing modules.
- Auto-configuration defaults that are explicitly verified for the resolved version.

## Change decision

Prefer this order:

1. Configure an existing Athena capability.
2. Implement a documented public SPI or replace a bean guarded by `@ConditionalOnMissingBean`.
3. Add a consumer-owned adapter around a public Athena contract.
4. Propose a framework change only when the behavior is generic across consumers and cannot be expressed safely through extension points.

Never fork or copy Athena internals merely to bypass dependency/version investigation.

## Capability maturity

Interpret artifact status from `framework-manifest.json`:

- `implemented`: contains Java runtime behavior; still verify activation conditions.
- `aggregation-only`: supplies dependencies but no Java implementation of its own.
- `resource-only`: supplies dependencies/resources but no Java implementation.
- `placeholder`: declared but supplies no active capability.
- `parent`: build/aggregation module; do not use as a runtime starter.
- `bom`: dependency-management import only.

An artifact being published does not prove that a requested behavior is implemented.
