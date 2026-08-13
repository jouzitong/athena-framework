# Module catalog

Use `framework-manifest.json` for the complete generated artifact list and source evidence. This guide explains selection intent.

## Core and Web

| Need | Direct artifact | Notes |
| --- | --- | --- |
| Common context/events/errors/utilities | `athena-framework-common` | Uses current source package names exactly. |
| MVC, error handling, response model, tracing | `athena-framework-starter-web` | Includes Spring Web/AOP/Actuator. |

Do not depend on `athena-project`, `athena-framework-starters`, or a `*-parent` artifact as runtime capabilities.

## Data

| Need | Direct artifact | Snapshot status |
| --- | --- | --- |
| Shared data API only | `athena-framework-starter-data-api` | Implemented contracts/models. |
| JDBC CRUD abstractions | `athena-framework-starter-data-jdbc` | Implemented; usually transitive. |
| MyBatis-Plus persistence | `athena-framework-starter-data-mybatis` | Implemented. Add Web directly when exposing controllers. |
| Spring Data JPA | `athena-framework-starter-data-jpa` | Implemented. Add Web directly when exposing controllers. |
| MongoDB | `athena-framework-starter-data-mongo` | Aggregation only; no Athena Mongo CRUD layer. |
| Dynamic routing | `athena-framework-starter-dynamic-datasource` | Implemented; disabled until configured. |

Add the actual JDBC driver in the consumer because drivers are provided-scope in persistence starters.

## Security

| Need | Direct artifact | Notes |
| --- | --- | --- |
| Security core | `athena-framework-starter-security` | Defaults to JWT token type but does not itself supply JWT TokenManager. |
| JWT token implementation | `athena-framework-starter-security-token-jwt` | Required for the default token mode. |
| Gateway/read-only aggregation | `athena-framework-starter-security-gateway` | Aggregates core + JWT; no Java code of its own. |
| JPA user/RBAC/audit | `athena-framework-starter-security-user-jpa` | Enable only the JPA user store. |
| MyBatis user/RBAC/audit | `athena-framework-starter-security-user-mybatis` | Enable only the MyBatis user store. |
| Annotation authorization | `athena-framework-starter-security-authorization` | Requires its explicit enabled property. |

The API and auth-core artifacts are normally transitive. Do not add them directly without a boundary reason.

## Cloud and integration

| Need | Artifact | Notes |
| --- | --- | --- |
| Nacos discovery/config | `athena-framework-starter-cloud-nacos` | Resource/dependency-only snapshot. |
| Feign clients | `athena-framework-starter-cloud-openfeign` | Implemented scanning, headers, options, decoder. |
| Seata | `athena-framework-starter-cloud-seata` | Implemented for MyBatis/SQL session presence. |
| Kafka | `athena-framework-starter-kafka` | Implemented; disabled until `athena.kafka.enabled=true`. |
| Unified communication | `athena-framework-starter-communication` | Core dispatcher. |
| Email/SMS/WeCom | corresponding `athena-framework-starter-communication-*` | Each channel is opt-in. |

## Realtime, storage, and observability

| Need | Artifact | Notes |
| --- | --- | --- |
| WebSocket | `athena-framework-starter-websocket` | Implemented; in-memory defaults require replacement for clusters. |
| MinIO | `athena-framework-starter-minio` | Implemented; disabled until configured. |
| Elasticsearch | `athena-framework-starter-es` | Thin implemented configuration; inspect version-specific API. |
| Logging | `athena-framework-starter-log` | Logback environment post-processor and bundled config. |

## Test family and AI

The `athena-framework-starter-test-*` family is an application test-plan/catalog/execution capability, not a substitute for JUnit or Spring Boot tests. Select its API, engine, HTTP/WebSocket executors, storage, catalog, and scheduler modules according to the application feature.

`athena-framework-starters-ai` is a placeholder in this snapshot. Do not claim Spring AI behavior from it or recommend it as an implementation dependency.
