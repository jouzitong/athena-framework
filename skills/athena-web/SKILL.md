---
name: athena-web
description: Design, implement, refactor, and verify HTTP APIs for Java and Spring projects consuming Athena Framework, including MVC controllers, request and response DTOs, validation, error handling, pagination, response envelopes, and API tests. Use when a consumer project needs Athena Web starter integration or a REST endpoint built on Athena contracts.
---

# Athena Web

Use this skill for the HTTP boundary of an Athena consumer project. Keep domain rules, DTO fields, authorization decisions, and API compatibility in the consumer project; use Athena Web for shared response, error, context, and MVC infrastructure.

## Required workflow

1. Read the consumer project's `AGENTS.md`, build files, and architecture notes. Ensure the project-local `./.codex/skills/athena-framework-core` is installed; do not use a developer's global Skill installation.
2. Inspect the project and recommend the starter before editing:

   ```bash
   python3 .codex/skills/athena-framework-core/scripts/inspect_project.py --project .
   python3 .codex/skills/athena-framework-core/scripts/recommend_modules.py --project . --features web
   ```

3. Read `./.codex/skills/athena-framework-core/references/architecture-and-boundaries.md`, `module-catalog.md`, `common-and-web.md`, and `project-conventions.md`. Inspect the resolved Athena JAR/source if the consumer version differs from `./.codex/skills/athena-framework-core/references/framework-manifest.json`.
4. Select `athena-framework-starter-web` as the direct Web artifact. Treat `athena-framework-common` and transitive modules as support, not as a reason to add duplicate dependencies.
5. Design the endpoint contract first: request validation, response envelope, error codes, pagination and sorting, idempotency, authentication boundary, and backward compatibility. Use `R`/`IR` and the current public controller contracts only after verifying their exact signatures.
6. Generate only repeatable edges with the core scaffold when useful:

   ```bash
   python3 .codex/skills/athena-framework-core/scripts/scaffold_feature.py --list
   python3 .codex/skills/athena-framework-core/scripts/scaffold_feature.py --template web-api --project . --name Example --package com.example.app
   ```

   Review the dry-run before `--write`; adapt names, fields, validation, authorization, and tests to the domain.
7. Verify with the consumer's unit/context/API tests and then run the core static validator and bounded verifier. Report compile, test, runtime smoke, and external-service status separately.

## Guardrails

- Do not put business controllers or domain DTOs in Athena packages.
- Do not claim an endpoint is protected merely because the Web starter is present; security is a separate design and dependency decision.
- Do not expose generic filter or sort fields without an allowlist.
- Named `application-athena-*.yml` files are not active until the consumer imports or activates them.

Read `./.codex/skills/athena-framework-core/references/common-and-web.md` for exact response/error/context behavior and `./.codex/skills/athena-framework-core/references/testing-and-acceptance.md` before claiming acceptance.
