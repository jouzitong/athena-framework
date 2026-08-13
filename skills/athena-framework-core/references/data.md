# Data development

## Choose a persistence path deliberately

- Use MyBatis for explicit SQL/MyBatis-Plus control.
- Use JPA for repository/specification-oriented persistence.
- Use JDBC/API artifacts only when building a custom implementation against Athena contracts.
- Treat Mongo as dependency aggregation; implement consumer-owned Mongo repositories and mapping.
- Add dynamic datasource only when routing rules and transaction behavior are explicitly designed.

The MyBatis and JPA starters declare Web and database drivers with provided scope. Add `athena-framework-starter-web` when using Athena controllers, and add the selected JDBC driver in the consumer.

## Current MyBatis chain

The source snapshot uses this chain:

```text
Entity extends org.athena.framework.data.mybatis.entity.BaseEntity
DTO extends org.athena.framework.data.mybatis.entity.dto.BaseDTO
Query extends org.athena.framework.data.jdbc.req.BaseRequest
Mapper extends org.athena.framework.data.mybatis.mapper.CrudMapper<Entity>
Convert implements org.athena.framework.data.jdbc.convert.IConvert<Entity, DTO>
Service extends org.athena.framework.data.mybatis.service.BaseMapperService<Entity, Mapper, DTO>
Controller extends org.athena.framework.data.jdbc.web.BaseController<DTO, Query, Service>
```

Do not use older examples that mention `MapperServiceImpl` or a MyBatis-specific base controller without verifying that those types exist in the resolved artifact.

## Current JPA chain

```text
Entity extends org.athena.framework.data.jpa.domain.BaseEntity
DTO extends org.athena.framework.data.jpa.domain.dto.BaseDTO
Query extends org.athena.framework.data.jdbc.req.BaseRequest
Repository extends org.athena.framework.data.jpa.repository.BaseRepository<Entity>
Convert implements IConvert<Entity, DTO>
Service extends org.athena.framework.data.jpa.service.BaseMapperService<Entity, DTO>
Controller extends JDBC BaseController<DTO, Query, Service>
```

Implement `entityType()`, `convert()`, and `repository()` in a JPA service.

## CRUD and query semantics

The shared controller contract exposes POST create, PUT full update, PATCH edit, DELETE, DELETE `/physical/{id}`, deprecated GET paging, POST `/_search`, and GET `/{id}`.

- `IConvert.updateEntityFromDto` applies nulls; `editEntityFromDto` ignores nulls by contract/annotations.
- `BaseRequest` uses `page`, `size`, `searchCount`, `sorts`, and the legacy property name `filedQueries`.
- MyBatis builds queries through `MybatisPlusWrapperUtils`; verify allowed fields/operators before exposing generic filters.
- Do not assume DELETE is soft. The interface comment, entity annotations, mapper behavior, and JPA repository implementation must all be checked for the selected version/entity.
- Define authorization and business invariants before inheriting generic CRUD routes.

## DDL safety

`lib.jdbc` snapshot defaults include `enable-create-table-ddl=true` and `auto-add-column=true`. Explicitly disable automatic DDL execution unless a reviewed environment requires it. Keep `auto-update-column` and `auto-drop-column` false in production.

For JPA, use `spring.jpa.hibernate.ddl-auto=validate` or `none` in production and keep `open-in-view=false` unless a reviewed design says otherwise.

## Dynamic datasource

Enable with `athena.datasource.dynamic.enabled=true`, define a real `primary`, and provide at least one datasource. Available routing inputs include operation rules, annotations, tenant mapping, and read/write groups.

Validate:

- Which strategy wins according to `strategy-order`.
- Transaction pinning and cross-datasource behavior.
- Tenant isolation and missing-route failure behavior.
- Credentials externalization.
- Health checks and pool configuration for every datasource.

## Required verification

Test mapping in both directions, PUT vs PATCH null semantics, paging/count behavior, filter allowlists, optimistic locking/version handling, delete semantics, transaction rollback, migrations, and the actual target database dialect.
