# Extension points

## Extension rule

Use public interfaces and missing-bean replacement points. Inspect the matching auto-configuration before defining a bean so its type/name and activation order are correct.

Avoid extending implementation classes unless the API intentionally requires a base class, such as Athena data services/controllers.

## Common

- `EventPublisher`: replace event publication while preserving event semantics.
- `IUserContextService`: bridge authenticated user/tenant/locale access.
- `ErrorCodeService` / providers: resolve consumer error catalogs.
- `AsyncTaskContextPropagator`: preserve required context in Athena async execution.
- `LockService`: replace the local lock when distributed coordination is required.

Do not mistake local lock/event/thread defaults for cross-process guarantees.

## Data

- `IConvert`: explicit Entity/DTO mapping and PUT/PATCH semantics.
- `IJdbcCrudInterceptor`: ordered validation/audit hooks around CRUD.
- Query conversion hooks on `BaseRequest` and MyBatis query utilities.
- Dynamic datasource `RouteStrategy` and route-decision engine.
- MyBatis metadata/DDL builder/parser interfaces.

Keep interceptors deterministic and avoid hidden business side effects after database writes.

## Security

- Identity/user: `SecurityUserRepository`, `IdentityProvider`.
- Credentials/authentication: `CredentialVerifier`, `Authenticator`.
- Token: `TokenManager`.
- Context: ordered `UserContextEnricher` implementations.
- Authorization: `AuthorizationProvider`, `PermissionEvaluator`, `RoleProvider`.
- Audit/menu: `AuditPublisher`, menu providers.

Use empty collections instead of null, preserve tenant isolation, externalize cryptographic material, and make context enrichers idempotent.

## WebSocket

- Security: `TokenService`, `AclService`.
- Protocol: `WsActionHandler`, `WsHandler`, `WsRouter`.
- Delivery: `MessageBus`, `TopicDispatcher`, `WsOutbound`.
- State: `SessionManager`, `SubscriptionManager`, `ResumeStore`, `ConnectionRegistry`.
- Operations: `WsMetrics`, outbound queue/backpressure components.

Replacing one in-memory component may require replacing related components to achieve real cross-instance semantics.

## Messaging, communication, storage

- Kafka `MessagePublisher` and dynamic consumer handler/manager.
- Communication `ChannelDriver` behind `CommunicationService`.
- MinIO exposes `ObjectStorageService`; wrap it at the business boundary rather than replacing it unless another storage provider must satisfy the same consumer port.

## Verification for an override

1. Prove the default bean is actually backed off.
2. Prove exactly one intended bean is active.
3. Test ordering when multiple implementations are supported.
4. Test missing dependencies/configuration.
5. Test failure behavior and observability.
6. Confirm no internal class leaked into the consumer's public contract.
