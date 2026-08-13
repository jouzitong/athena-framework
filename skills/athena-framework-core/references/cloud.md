# Cloud integration

## Nacos

`athena-framework-starter-cloud-nacos` is resource/dependency-only in this snapshot. It aggregates Nacos discovery/config, an explicit Nacos client, common, and Spring Cloud bootstrap support. It does not supply Java auto-configuration of its own.

Configure standard `spring.cloud.nacos.*` properties in bootstrap/config data according to the resolved Spring Cloud Alibaba version. Externalize server address and credentials. Verify discovery registration and remote configuration separately.

## OpenFeign

`athena-framework-starter-cloud-openfeign` supplies:

- Configurable scanning of standard `@FeignClient` interfaces.
- Connection/read timeout options.
- Athena error decoder.
- No retries by default.
- Propagation of trace, authorization, token, user, tenant, locale, and application-name headers when a servlet request is available.

The default base package is `org.athena`, which will not discover normal consumer clients. Set:

```yaml
athena:
  cloud:
    openfeign:
      base-packages:
        - com.example.project.client
```

Validate fallback classes, timeouts, idempotency before introducing retries, and whether propagated identity headers are trusted by the destination.

## Seata

`athena-framework-starter-cloud-seata` activates when `SqlSessionFactory` is present and `athena.cloud.seata.enabled` is true or missing. The snapshot imports a MyBatis/Seata datasource configuration and defaults proxy mode to `AT`.

Verify all of the following before claiming distributed transactions work:

- Seata server/registry/configuration connectivity.
- Transaction group mapping.
- Supported database and undo-log schema.
- DataSource proxy mode.
- MyBatis participation and rollback behavior across services.
- Timeout, retry, idempotency, and compensation behavior.

Do not add Seata merely because more than one datasource exists; local multi-datasource transactions and distributed service transactions are different problems.

## Compatibility

Use the Athena BOM's Spring Boot, Spring Cloud, and Spring Cloud Alibaba alignment. A consumer overriding one of these platforms must prove compatibility through effective-POM inspection, dependency-tree comparison, context startup, and integration tests.
