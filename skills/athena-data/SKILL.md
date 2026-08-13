---
name: athena-data
description: Design, implement, refactor, and verify persistence for Java and Spring projects consuming Athena Framework, with concrete reference code for entities, DTOs, queries, MyBatis mappers, JPA repositories, converters, services, controllers, Mongo repositories, configuration, transactions, and database tests. Use when a consumer project needs an Athena data layer, CRUD module, repository, mapper, service, controller, or datasource routing.
---

# Athena Data

Use this skill to choose and implement one persistence boundary at a time. Athena supplies reusable contracts and selected CRUD infrastructure; the consumer owns entities, schema, migrations, query allowlists, transactions, and business invariants.

The reference code is organized by persistence technology. Read only the relevant file before implementation:

- MyBatis CRUD: `references/mybatis-crud-example.md`
- JPA CRUD: `references/jpa-crud-example.md`
- Mongo CRUD: `references/mongo-crud-example.md`
- Shared framework semantics: `./.codex/skills/athena-framework-core/references/data.md`

Each reference contains a complete Entity/DTO/Query/Mapper-or-Repository/Convert/Service/Controller chain, configuration, endpoint mapping, and verification checklist. Adapt package names, fields, validation, authorization, schema, and tests to the consumer domain; do not copy business names as if they were framework APIs.

## Choose the path

| Need | Direct artifact | Boundary |
| --- | --- | --- |
| MyBatis-Plus CRUD | `athena-framework-starter-data-mybatis` | Use Athena MyBatis entity/mapper/service contracts; add Web when exposing controllers. |
| JPA CRUD | `athena-framework-starter-data-jpa` | Use Athena JPA entity/repository/service contracts; add Web when exposing controllers. |
| MongoDB | `athena-framework-starter-data-mongo` | Aggregation only in the current snapshot; repositories and mappings remain consumer-owned. |
| Custom JDBC contracts | `athena-framework-starter-data-jdbc` | Implement the consumer adapter against public interfaces. |
| Routing/multiple databases | `athena-framework-starter-dynamic-datasource` | Add only with explicit routing, transaction, tenant, and failure design. |

## Required workflow

1. Read `AGENTS.md`, inspect the consumer, and run the core recommender with the requested feature (`mybatis`, `jpa`, `mongo`, or `dynamic-datasource`).
2. Read the matching reference code file in this Skill, then read the project-local core files under `./.codex/skills/athena-framework-core/references/`: `data.md`, `module-catalog.md`, `architecture-and-boundaries.md`, and `project-conventions.md`. Confirm the resolved Athena version before using APIs.
3. Draw the consumer's layer chain before editing:
   - MyBatis: `Entity -> DTO -> Query -> Mapper -> Convert -> Service -> Controller`.
   - JPA: `Entity -> DTO -> Query -> Repository -> Convert -> Service -> Controller`.
   - Mongo: `Document -> Repository -> Service -> Controller`; this is consumer-owned Spring Data code, not Athena's JDBC CRUD chain.
4. Add the actual database driver in the consumer. Do not assume a persistence starter supplies a runtime driver because its driver may be provided-scope.
5. Use the core `data-mybatis-crud` or `data-jpa-crud` scaffold only after reviewing its dry-run. Use the Mongo reference as the implementation starting point because the current Athena Mongo artifact is aggregation-only and has no Athena Mongo CRUD scaffold.
6. Implement in this order: schema/entity fields, DTO input/output boundary, query and allowlists, persistence interface, converter, service transaction/invariants, controller authorization, configuration, then tests. State every omitted layer and why.
7. Define and test PUT versus PATCH null behavior, paging/count, sort/filter allowlists, optimistic locking, soft/physical delete semantics, rollback, migration compatibility, and the target dialect.
8. Disable automatic schema mutation in production. For JPA prefer `ddl-auto=validate` or `none` and explicitly review `open-in-view`; for dynamic routing test strategy precedence, transaction pinning, tenant isolation, missing-route failure, and health checks.
9. Run core static validation, focused persistence tests, and Maven verification. Run `codegraph sync` when required by the consumer project.

## Guardrails

- Do not use obsolete `MapperServiceImpl` or invent a MyBatis-specific controller; inspect the exact resolved artifact.
- Do not enable both JPA and MyBatis security user stores as a side effect of data work.
- Do not expose generic query operators without a field/operator allowlist.
- Do not treat Mongo's aggregation artifact status as an Athena Mongo CRUD implementation.
- Do not copy database credentials or production DDL settings into templates.

Read the selected reference code file for the implementation shape, the project-local core `./.codex/skills/athena-framework-core/references/data.md` for exact CRUD semantics, and `./.codex/skills/athena-framework-core/references/testing-and-acceptance.md` before claiming completion.
