# Testing and acceptance

## Separate test layers

Use normal project tests even when Athena's test starter family is present:

1. Unit tests for business rules, mappings, and adapters.
2. Spring slice/context tests for controller, persistence, security, and bean-replacement behavior.
3. Integration tests with the selected database/broker/object store/provider.
4. Runtime smoke tests for auto-configuration and external connectivity.
5. Contract/acceptance tests for public HTTP/WebSocket/event behavior.

The `athena-framework-starter-test-*` family implements test catalog, plan, executor, storage, HTTP, WebSocket, and scheduler capabilities. It is application functionality, not a replacement for JUnit, Mockito, Testcontainers, or build verification.

## Build scope

The current Athena root POM does not aggregate `athena-framework-test`. Likewise, a consumer's root build may omit profiles or sample modules. Inspect active Maven modules and profiles before claiming the examples or integration tests ran.

Prefer:

```bash
./mvnw --batch-mode --no-transfer-progress test
./mvnw --batch-mode --no-transfer-progress -pl <module> -am test
```

Use the skill's `verify_project.py` for bounded execution and a redacted result summary.

## Minimum acceptance by area

| Area | Required evidence |
| --- | --- |
| BOM/starter change | Effective POM or dependency tree plus compile/test |
| Web | success, validation, business error, unknown error, serialization |
| MyBatis/JPA | mapping, CRUD semantics, paging/filtering, rollback, real dialect |
| Security | valid/invalid/expired token, credential hashing, allow/deny, tenant isolation |
| OpenFeign | discovery/URL, headers, timeout, decoder, fallback |
| Seata | real multi-participant commit and rollback |
| Kafka | publish, consume, ack, duplicate/retry/DLQ behavior |
| WebSocket | handshake, ACL, routing, reconnect/resume, slow client |
| MinIO | put/get/delete/stat/presign and authorization |
| Communication | routing, template validation, provider failure/sandbox delivery |

## Result language

Report precisely:

- `implemented`: source/config changes exist.
- `static validation passed`: the skill found no static errors; warnings may remain.
- `compiled`: compiler succeeded for named modules.
- `tests passed`: identify which test command and modules.
- `context started`: name the profile and external services mocked/real.
- `integration verified`: identify the real dependency and scenario.
- `not verified`: state missing service, credential, environment, or test coverage.

Do not turn a generated template, clean dependency tree, or successful unit test into a claim of production readiness.

## Skill template validation

When the skill itself changes, validate:

- `quick_validate.py` for Skill structure/frontmatter.
- Every script's `--help` and representative commands.
- `refresh_framework_snapshot.py --check` against the intended Athena checkout.
- Template catalog completeness and unresolved placeholders.
- Dry-run overwrite protection.
- Forward tests in clean temporary consumer projects, without leaking the expected solution.
