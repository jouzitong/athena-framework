---
name: athena-realtime
description: Design, implement, refactor, and verify Athena WebSocket features for Java and Spring projects, including handshake authentication, sessions, routing, actions, ACL, resume, backpressure, outbound delivery, and clustered replacement points. Use when a consumer project needs Athena real-time communication.
---

# Athena Realtime

Use this skill for WebSocket protocol and runtime integration. Keep message schema, user/session authorization, reconnect semantics, rate limits, and cluster topology explicit in the consumer project.

## Required workflow

1. Read the consumer `AGENTS.md`, inspect the project, and run the core recommender with `websocket` and `security` when authentication is involved.
2. Read the project-local `./.codex/skills/athena-framework-core/references/websocket.md`, `security.md`, `extension-points.md`, and `testing-and-acceptance.md`. Inspect the exact version's public interfaces.
3. Design handshake and token extraction, session identity, route/action protocol, authorization, error frames, heartbeat, reconnect/resume, backpressure, outbound delivery, and observability before implementing handlers.
4. Select `athena-framework-starter-websocket`. Treat the default in-memory session manager and connection registry as single-instance behavior; define a distributed replacement before claiming cluster support.
5. Use the `websocket-action` scaffold only after a dry-run review. Keep handlers thin and delegate business operations to consumer services with explicit authorization and idempotency.
6. Test handshake denial, expired/malformed tokens, route denial, malformed frames, duplicate/replayed actions, reconnect/resume, slow consumers, disconnect cleanup, and cluster behavior where applicable.
7. Run core static validation and focused WebSocket tests. Report whether an actual broker/cluster/client smoke test ran.

## Guardrails

- Do not treat an open handshake as authorization for every action.
- Do not claim in-memory defaults are cluster-safe.
- Do not allow unbounded outbound queues or unbounded message sizes.
- Do not log raw credentials or sensitive frame payloads.

Read the project-local `./.codex/skills/athena-framework-core/references/websocket.md` for protocol and extension details.
