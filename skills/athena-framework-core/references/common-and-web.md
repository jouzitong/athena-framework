# Common and Web

## Common module

The snapshot exposes common types primarily under the legacy `org.arthena.framework.common` package. Verify imports against the resolved version.

Relevant contracts include:

- `BizException` and framework error-code providers.
- `SystemContext`, request-header constants, and async context propagators.
- `EventPublisher` / `IEvent`, backed by Spring application events by default.
- `AsyncTaskExcutor` and context propagators for MDC/system context.
- `LockService`, whose default is local-process locking only.
- `IUserContextService`, error services, enums, JSON/date/object utilities.

Prefer standard JDK/Spring APIs when Athena adds no stable project-wide contract. Do not spread static context or utility calls through domain logic when an injected port is clearer.

## Web starter behavior

`athena-framework-starter-web` activates `WebAutoConfig`, component-scans Athena Web classes, and brings Web, AOP, Actuator, common, and validation dependencies.

The `R<D>` model contains:

- `code`, where zero is success.
- `data`, `msg`, `timestamp`, and `traceId`.
- Optional `sign` and `signKeyId` fields.
- Static `ok(...)` and `fail(...)` factories.

`BaseControllerAdvice` handles `BizException`, validation/binding errors, malformed requests, unsupported methods/media, and otherwise maps exceptions to the framework unknown-error response.

## Response decision

Before returning `R` manually, inspect whether the target project already has response wrapping/interception. Avoid `R<R<T>>` and avoid changing an existing public API envelope accidentally.

If a controller returns business DTOs directly, verify whether project-level advice or interceptors wrap them. If no wrapper exists and the contract requires Athena's envelope, return `R.ok(data)` explicitly.

## Tracing and signing

- Trace IDs are populated from the Web trace filter/MDC when present.
- Response signing is configured under `lib.web.sign`.
- Never use a literal signing secret. Keep signing disabled unless consumers verify signatures and rotation behavior.
- Configure `lib.web.enum-packages` only for packages that intentionally expose enum metadata.

## HTTP verification

At minimum verify:

1. Successful response serialization.
2. Validation failure payload and HTTP status.
3. `BizException` code/status mapping.
4. Unexpected exception behavior without sensitive detail leakage.
5. Trace ID propagation.
6. Response signature only when enabled.
