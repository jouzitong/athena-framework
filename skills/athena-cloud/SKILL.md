---
name: athena-cloud
description: Design, implement, refactor, and verify cloud-native integration for Java and Spring projects consuming Athena Framework, including Nacos discovery and configuration, OpenFeign clients, request headers, timeouts, error decoding, Seata data-source integration, and service-boundary tests. Use when a consumer project needs Athena Cloud starter selection or distributed-service integration.
---

# Athena Cloud

Use this skill for service-to-service and cloud-runtime boundaries. Keep service contracts, timeout budgets, retry policy, idempotency, failure semantics, and deployment topology explicit in the consumer project.

## Required workflow

1. Read `AGENTS.md`, inspect the consumer project, and run the core recommender with `nacos`, `openfeign`, or `seata`.
2. Read the project-local `./.codex/skills/athena-framework-core/references/cloud.md`, `dependency-versioning-and-repositories.md`, `architecture-and-boundaries.md`, and `troubleshooting.md`. Compare the consumer's resolved versions with `./.codex/skills/athena-framework-core/references/framework-manifest.json`.
3. Classify modules before adding them:
   - `athena-framework-starter-cloud-nacos` is resource/dependency-oriented in the current snapshot; do not claim it alone implements a full discovery/config runtime.
   - `athena-framework-starter-cloud-openfeign` provides the implemented Feign scanning/header/options/decoder path; define client contracts in the consumer.
   - `athena-framework-starter-cloud-seata` supplies the implemented SQL/MyBatis integration path; verify transaction topology and actual Seata runtime separately.
4. Design registration/config source, namespace/group, client package scanning, headers and trace propagation, connect/read timeouts, retry/circuit-breaker behavior, error mapping, fallback semantics, and distributed transaction boundaries before editing POM/config.
5. Treat generated `bootstrap-athena-nacos.yml` or other named fragments as inactive until explicitly imported/activated. Externalize repository, Nacos, Seata, and service credentials.
6. Test Feign contract/decoder/header behavior, timeout and failure mapping, idempotent retries, discovery/config absence, transaction rollback, and local-vs-deployed configuration differences.
7. Run core static validation and Maven verification. Separate compilation evidence from live Nacos/Seata/registry smoke evidence.

## Guardrails

- Do not add cloud starters merely because a project is deployed in a cloud; select by runtime capability.
- Do not hide non-idempotent side effects behind automatic retries.
- Do not claim service discovery, config-center availability, or distributed transaction success without the corresponding external runtime.
- Do not print Maven, registry, or cloud credentials.

Read the project-local `./.codex/skills/athena-framework-core/references/cloud.md` and `./.codex/skills/athena-framework-core/references/testing-and-acceptance.md` for exact compatibility and evidence rules.
