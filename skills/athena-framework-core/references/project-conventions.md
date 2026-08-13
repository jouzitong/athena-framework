# Consumer project conventions

## Respect local rules first

Read the consumer's `AGENTS.md`, README, parent POM, architecture decisions, and neighboring implementations. Athena conventions do not override an explicit business-project contract.

## Package by ownership

Keep generated and edited code under the consumer's package root. A practical default is:

```text
<base-package>/
  model/            requests, responses, DTOs, queries
  domain/           business rules and ports
  service/          use cases and Athena adapters
  persistence/      entities, repositories, mappers
  web/              HTTP controllers
  security/         identity, context, authorization adapters
  messaging/        Kafka and communication adapters
  websocket/        ACL, token, action handlers
```

Do not put consumer classes under `org.athena.*` or `org.arthena.*`.

## Preserve exact framework names

The current source contains both `org.athena.framework...` and legacy `org.arthena.framework...` packages. It also contains legacy spellings such as `filedQueries`, `serivce`, and `AsyncTaskExcutor`.

- Import the exact type exposed by the resolved version.
- Do not silently "correct" a framework package or symbol in consumer code.
- Hide awkward legacy names behind consumer-owned adapters when they would otherwise leak into a public business contract.

## Model boundaries

- Keep persistence entities internal to the persistence boundary.
- Use DTO/request/response objects for transport and validation.
- Map explicitly when update semantics matter. Athena `IConvert` distinguishes full update from null-ignoring edit.
- Do not expose a generic base CRUD API when the business contract needs different routes, authorization, invariants, or lifecycle semantics.
- Treat Athena base controllers as an opt-in shortcut, not a mandatory architecture.

## Configuration

- Prefer kebab-case Spring keys in new YAML.
- Externalize endpoints, usernames, passwords, tokens, and signing keys.
- Keep opt-in modules explicitly enabled or disabled.
- Separate safe local defaults from production configuration.
- Avoid silent schema mutation, permissive WebSocket authentication, and default security secrets.

## Error and response contracts

Use the consumer's established response contract. Athena Web supplies `R`, `BizException` handling, validation mapping, timestamps, trace IDs, and optional response signing. Do not double-wrap responses or add a second global exception advice without checking ordering and payload compatibility.

Define business error codes in the consumer. Do not reuse an unrelated framework error code because its message appears convenient.

## Tests and comments

- Test business behavior independently from framework plumbing.
- Add context-start or slice tests for auto-configuration and bean replacement.
- Test database/write semantics, authorization failures, serialization, and external-service boundaries.
- Write comments for business boundaries, non-obvious framework behavior, and operational constraints; do not translate method names into comments.
