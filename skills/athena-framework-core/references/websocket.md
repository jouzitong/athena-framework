# WebSocket development

## Runtime chain

The snapshot registers the configured endpoint (default `/ws/v1`) and routes this flow:

```text
handshake -> TokenService -> session creation -> decode/validate
  -> WsRouter -> ACL -> WsHandler/WsActionHandler -> WsOutbound
```

The starter also manages subscriptions, local topic dispatch, resume snapshots, queues/backpressure, and metrics.

## Security defaults

The default `TokenService` and `AclService` are permissive allow-all implementations. Replace both for any authenticated or authorized application.

- `TokenService.parse` must return a trusted `TokenInfo` or reject invalid input according to the handshake behavior.
- `AclService` separately controls subscribe, publish, and request actions.
- Keep topic/action authorization server-side; client UI restrictions are not controls.
- Test direct connections that bypass a gateway.

## Extension points

Common replaceable contracts include:

- `WsActionHandler` for REQUEST action extensions.
- `WsHandler` for protocol message types.
- `MessageBus` / `TopicDispatcher` for distributed delivery.
- `SessionManager`, `SubscriptionManager`, and `ResumeStore`.
- `ConnectionRegistry`, `WsOutbound`, and `WsMetrics`.
- `TokenService` and `AclService`.

Use `WsMessageFactory` for standard response/error shapes where appropriate. Preserve request IDs for request/response correlation.

## Cluster boundary

Default message bus, sessions, subscriptions, resume store, and connection registry are in-memory/local. They do not provide cross-instance subscriptions or recovery.

For a cluster, design ownership and replace the required stores/bus. Verify fan-out, duplicate delivery, instance loss, resume TTL, stale subscriptions, and ordering. Do not represent the local defaults as horizontally scalable behavior.

## Protocol rules

- Require version, type, and timestamp according to the framework validator.
- Use `requestId` for requests/responses.
- Use stable, authorization-aware topic and action names.
- Bound payload size and validation cost.
- Avoid putting secrets in payload/meta or logs.
- Make event handlers resilient to reconnects and duplicate events.

## Capacity and backpressure

Review endpoint, maximum connections/message/frame sizes, per-connection ingress rate, handshake rate, idle/heartbeat intervals, outbound queue limit, slow-connection threshold, topic throttle, resume TTL, and backpressure strategy.

Defaults are starting points, not capacity proof. Load-test realistic message sizes, fan-out, slow clients, reconnect storms, and downstream pauses.

## Verification

Test valid/invalid handshake, unauthorized topic/action, subscribe/unsubscribe, request correlation, malformed/oversized messages, slow-client behavior, heartbeat timeout, reconnect/resume, duplicate delivery, and cluster replacement implementations when used.
