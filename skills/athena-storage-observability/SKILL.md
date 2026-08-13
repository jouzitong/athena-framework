---
name: athena-storage-observability
description: Design, implement, refactor, and verify object storage, Elasticsearch, and logging integration for Java and Spring projects consuming Athena Framework, including MinIO facades, bucket and URL policy, Elasticsearch version checks, logging configuration, secrets, and operational evidence. Use when a consumer project needs Athena storage or observability capabilities.
---

# Athena Storage and Observability

Use Athena adapters at infrastructure boundaries while keeping bucket policy, object metadata, index mappings, retention, log governance, and operational alerts in the consumer/platform design.

## Required workflow

1. Read `AGENTS.md`, inspect the consumer project, and run the core recommender with `minio`, `elasticsearch`, or `logging`.
2. Read the project-local `./.codex/skills/athena-framework-core/references/storage-and-observability.md`, `architecture-and-boundaries.md`, `dependency-versioning-and-repositories.md`, and `testing-and-acceptance.md`.
3. For MinIO, select `athena-framework-starter-minio`, review `athena.minio.enabled` and endpoint/credential/bucket requirements, and use the `object-storage-facade` scaffold as a consumer-facing adapter. Define content type, size, path, tenant, retention, download, and presigned-URL authorization rules.
4. For Elasticsearch, treat the starter as a thin implemented configuration entry. Inspect the resolved client/version and design mappings, aliases, index lifecycle, reindex, retention, and failure behavior in the consumer.
5. For logging, select `athena-framework-starter-log` only when its environment post-processor and bundled configuration match the deployment. Keep paths, rollover, redaction, correlation IDs, and access policy explicit.
6. Externalize MinIO, Elasticsearch, and repository credentials. Never copy defaults into production configuration or print resolved secret values.
7. Test object authorization and cleanup, provider outage, presigned URL expiry, large-object behavior, index compatibility/reindex, log redaction, rollover, correlation, and health/readiness behavior.
8. Run core static validation and bounded tests, and state whether real storage/ES infrastructure was exercised.

## Guardrails

- A successful object upload does not prove download authorization or retention compliance.
- Do not use a presigned URL as a substitute for application authorization.
- Do not infer Elasticsearch repository APIs from the artifact name; verify the resolved version.
- Do not expose secrets or sensitive payloads in logs.

Read the project-local `./.codex/skills/athena-framework-core/references/storage-and-observability.md` for current module boundaries.
