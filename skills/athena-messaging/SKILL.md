---
name: athena-messaging
description: Design, implement, refactor, and verify Kafka event flows and Athena communication channels for Java and Spring projects, including publishers, dynamic consumers, email, SMS, WeCom, templates, retries, idempotency, delivery failure, and secret configuration. Use when a consumer project needs Athena messaging or notification integration.
---

# Athena Messaging

Design messages as durable contracts and notifications as controlled delivery capabilities. The consumer owns event schemas, business idempotency, outbox or transaction coordination, templates, recipients, and operational policy.

## Required workflow

1. Read the consumer `AGENTS.md`, inspect the POM/config, and run the core recommender with `kafka`, `communication`, and the required channel (`email`, `sms`, or `wecom`).
2. Read the project-local `./.codex/skills/athena-framework-core/references/messaging-and-communication.md`, `cloud.md`, `architecture-and-boundaries.md`, and `testing-and-acceptance.md`.
3. For Kafka, select `athena-framework-starter-kafka`, review its conditional activation, bootstrap servers, consumer group, serialization, dynamic-consumer lifecycle, retry/DLQ, ordering, partitioning, and delivery semantics. Use the `kafka-flow` scaffold only as a consumer adapter starting point.
4. For notifications, select the core communication starter plus only the channel drivers required. Use `CommunicationService` through a business-facing adapter; keep provider credentials, templates, recipient policy, rate limits, and failure handling in the consumer.
5. Activate named configuration deliberately. Set `athena.kafka.enabled=true` only with reviewed broker/group settings. Email/SMS/WeCom drivers remain opt-in and require externalized credentials.
6. Test serialization compatibility, duplicate delivery, restart/rebalance, out-of-order events, retry exhaustion, DLQ behavior, notification provider failure, template rendering, rate limits, and idempotent side effects.
7. Run core static validation and bounded tests. Report whether a real broker/provider was exercised.

## Guardrails

- Do not call at-least-once delivery exactly-once without proof of the full transaction/idempotency design.
- Do not publish arbitrary user content through controlled notification channels when template governance is required.
- Do not copy Kafka, SMS, email, WeCom secrets or enable providers in production by default.
- Do not hide external delivery failures behind a successful business response.

Read the project-local `./.codex/skills/athena-framework-core/references/messaging-and-communication.md` for current APIs and provider boundaries.
