---
name: athena-framework-core
description: Shared foundation for designing, implementing, troubleshooting, and verifying Java and Spring projects that consume Athena Framework. Use when a consumer project needs Athena BOM and starter selection, framework-version inspection, module status, configuration validation, scaffolding, or build and test verification; install this core skill together with any Athena domain skill.
---

# Athena Framework Core

Use Athena as a dependency-provided engineering layer. Keep business semantics in the consumer project and use public Athena contracts instead of copying framework internals.

This is the shared project-local dependency of the installable Athena domain skills. The distribution installer places it beside the selected domain Skill under the consumer project's `.codex/skills`; never require a developer to install an application-level Athena Skill.

## Follow the required workflow

1. Read every applicable `AGENTS.md` and the target project's build and architecture documentation before searching or editing.
2. Run the project inspector before proposing Athena APIs or dependencies:

   ```bash
   python3 .codex/skills/athena-framework-core/scripts/inspect_project.py --project .
   ```

3. Identify the consumer's Athena BOM version, Java baseline, current starters, persistence choice, configuration keys, and repository source.
4. Read `references/framework-manifest.json` and compare its `framework.revision` with the consumer's resolved Athena version. Treat the manifest as a source snapshot, not proof for a different version.
5. Load only the domain references required by the task. Use the routing table below. Domain skills in this distribution point to the same files.
6. Inspect the dependency JAR or matching Athena source when the consumer version differs from the bundled snapshot, when an API is absent from the manifest, or when source and prose disagree. Never invent a class, method, property, default, endpoint, or transitive dependency.
7. State the selected modules and why before implementation. Separate implemented modules, dependency aggregators, resource-only modules, and placeholders.
8. Make the smallest consumer-side change that satisfies the business request. Prefer public SPIs and `@ConditionalOnMissingBean` replacement points over framework forks.
9. Run static validation and then build/test verification:

   ```bash
   python3 .codex/skills/athena-framework-core/scripts/validate_project.py --project .
   python3 .codex/skills/athena-framework-core/scripts/verify_project.py --project . --mode test
   ```

10. Report the exact verification performed. Distinguish build success, test success, runtime smoke verification, baseline warnings, and unverified external services.

## Route references by task

- Read `references/architecture-and-boundaries.md` for every new Athena adoption or architecture decision.
- Read `references/dependency-versioning-and-repositories.md` for BOM, Maven repositories, JDK, upgrades, or dependency conflicts.
- Read `references/module-catalog.md` for starter selection and maturity status.
- Read `references/project-conventions.md` before generating packages, APIs, DTOs, errors, or configuration.
- Read `references/common-and-web.md` for common context, events, exceptions, response handling, Web MVC, and HTTP APIs.
- Read `references/data.md` for JDBC abstractions, MyBatis, JPA, Mongo, dynamic data sources, CRUD semantics, and transactions.
- Read `references/security.md` for authentication, JWT/local token modes, user persistence, RBAC, authorization, audit, or gateway security.
- Read `references/cloud.md` for Nacos, OpenFeign, Seata, service discovery, or distributed transactions.
- Read `references/messaging-and-communication.md` for Kafka, email, SMS, WeCom, and channel extensions.
- Read `references/websocket.md` for WebSocket protocol, sessions, ACL, handlers, resume, backpressure, and cluster replacement points.
- Read `references/storage-and-observability.md` for MinIO, Elasticsearch, logging, and operational configuration.
- Read `references/testing-and-acceptance.md` before claiming completion or using Athena's test starter family.
- Read `references/extension-points.md` before overriding Athena behavior or adding framework-integrated infrastructure.
- Read `references/troubleshooting.md` for startup failure, missing auto-configuration, dependency resolution, configuration binding, or behavior drift.

## Use deterministic helpers

- Run `scripts/recommend_modules.py --project . --features web,mybatis` to produce a version-aware starter recommendation. Review the output before changing `pom.xml`.
- Run `scripts/scaffold_feature.py --list` to see available templates. It is dry-run by default; pass `--write` only after reviewing destinations. It refuses overwrites unless `--force` is explicit.
- Treat generated `application-athena-*.yml` and `bootstrap-athena-nacos.yml` files as named-profile fragments. Merge, import, or activate them according to the consumer's configuration strategy; file presence alone does not activate those settings.
- Run `scripts/refresh_framework_snapshot.py --framework /path/to/athena-framework --check` when maintaining the skill. Write a new manifest only when intentionally synchronizing to that framework checkout.
- Prefer `./mvnw` when present. Do not assume a root Athena build covers sample applications; inspect the active Maven module list.
- Follow the consumer project's CodeGraph instructions when available; run `codegraph sync` after edits and never commit `.codegraph/`.

## Apply source-of-truth priority

Use this order when facts conflict:

1. Applicable consumer-project instructions and business requirements.
2. The consumer's resolved dependency tree and exact Athena artifacts.
3. Matching-version Athena bytecode/source and auto-configuration resources.
4. `references/framework-manifest.json` when its revision matches.
5. Curated domain references and templates.
6. General Spring knowledge.

Flag disagreement instead of silently choosing a convenient source.

## Preserve safety and framework boundaries

- Do not place business concepts in Athena packages or modify the framework merely to avoid a consumer-side adapter.
- Do not depend on implementation classes when a public API or SPI exists.
- Do not copy default secrets. Require environment-backed values for JWT signing, MinIO, SMS, WeCom, database, and repository credentials.
- Do not print Maven settings credentials or secret configuration values. The helper scripts redact sensitive values.
- Do not enable destructive schema management in production. Treat JPA `ddl-auto=update/create/create-drop` and automatic DDL generation as explicit-risk choices.
- Do not claim an aggregator or placeholder supplies runtime behavior.
- Do not introduce both JPA and MyBatis security user stores unless the framework version explicitly supports and the project deliberately configures one active implementation.
- Do not overwrite existing source with templates without reviewing a dry-run and the consumer's local conventions.

## Scaffold only repeatable edges

Use `assets/templates/` for verified boilerplate. Adapt names, packages, fields, validation, authorization, persistence schema, and tests to the target domain. Do not treat a template as a business design.

When the requested pattern is not represented, implement it from the matching public API rather than forcing the closest template.
