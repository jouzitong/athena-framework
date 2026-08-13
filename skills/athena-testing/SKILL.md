---
name: athena-testing
description: Plan, implement, troubleshoot, and verify tests for Java and Spring projects consuming Athena Framework, including context, Web, MyBatis, JPA, Security, WebSocket, Cloud, Kafka, storage, communication, static validation, dependency checks, and acceptance evidence. Use when a consumer project needs Athena test selection or a completion audit.
---

# Athena Testing

Use this skill to turn an Athena feature into layered evidence. The Athena test starter family adds application test-plan/catalog/execution capabilities; it does not replace JUnit, Mockito, Spring Boot tests, Testcontainers, Maven verification, or real external-service smoke tests.

## Required workflow

1. Read the consumer `AGENTS.md`, build docs, and feature design. Ensure the project-local `./.codex/skills/athena-framework-core` is installed.
2. Inspect before testing, then read the project-local `./.codex/skills/athena-framework-core/references/testing-and-acceptance.md`, `module-catalog.md`, and the feature reference relevant to Web, data, security, cloud, messaging, realtime, or storage.
3. Run the core static checks:

   ```bash
   python3 .codex/skills/athena-framework-core/scripts/inspect_project.py --project .
   python3 .codex/skills/athena-framework-core/scripts/validate_project.py --project .
   python3 .codex/skills/athena-framework-core/scripts/verify_project.py --project . --mode test
   ```

4. Add focused consumer tests for contracts and denial/failure paths. For data test mapping, query semantics, transactions, migrations, and the real dialect. For security test denial and secret handling. For external integrations distinguish mocked, containerized, local, and deployed evidence.
5. Select Athena test modules only when the application needs Athena's test-plan/catalog/execution functionality. Verify the actual module family and active Maven module list; do not infer behavior from an aggregator.
6. Run `./mvnw` when available, otherwise the project's declared Maven command. Follow CodeGraph instructions and run `codegraph sync` after edits.
7. Report static validation, compilation, unit/integration tests, runtime smoke, external dependency coverage, warnings, and unverified assumptions separately.

## Skill-maintenance validation

When changing this Skill distribution, run the official Skill validator for every `skills/*` folder, check the framework snapshot against this checkout, exercise the installer in a temporary target, and verify the core scaffold catalog has no unresolved placeholders. Do not commit `.codegraph/` or temporary consumer projects.

## Guardrails

- Passing a context test does not prove API, data, security, or external-service behavior.
- Do not claim live integration from a mock or compilation-only result.
- Keep secrets redacted in reports and logs.
- Treat warnings as findings to report, not silent failures to ignore.

Read the project-local `./.codex/skills/athena-framework-core/references/testing-and-acceptance.md` for the acceptance vocabulary and evidence rules.
