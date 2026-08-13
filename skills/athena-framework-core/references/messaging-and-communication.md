# Messaging and communication

## Kafka

`athena-framework-starter-kafka` is disabled until `athena.kafka.enabled=true`. Required snapshot properties include `bootstrap-servers` and `consumer-group-id`.

Public integration points include:

- `MessagePublisher.send(topic, payload)` and keyed/header variants.
- `DynamicKafkaConsumerManager` for explicitly created consumers.
- `DynamicKafkaConsumerHandler` receiving the record and acknowledgment.

The consumer defaults disable auto commit. A handler decides when to acknowledge. Design duplicate handling, idempotency, retry/dead-letter behavior, ordering, serialization, schema evolution, and observability before using the template.

Never log full sensitive event payloads. Validate broker authentication/TLS configuration separately because Athena's high-level properties do not model every production Kafka setting.

## Unified communication

The communication family provides:

- `CommunicationService` as the dispatch entry.
- `SendRequest`, receivers, channel type, template code/content, parameters, and attributes.
- `ChannelDriver` as the channel extension boundary.
- Email, SMS, and WeCom driver starters.

The common module defaults `allowDirectContent=true`. For controlled production messaging, prefer reviewed templates and set direct content to false when arbitrary content must not bypass template governance.

## Channel activation

Every channel starter is opt-in:

- Email: `athena.communication.email.enabled=true`; configure sender and Spring mail infrastructure.
- SMS: enable and externalize access key ID/secret, region, endpoint, and sign name.
- WeCom: enable and externalize corp ID/secret and agent ID.

Do not enable a channel with empty environment placeholders merely to make a context test pass. Validate provider connectivity in an integration environment.

## Business boundary

Build a consumer-owned sender around `CommunicationService` that chooses:

- Business type and template code.
- Allowed receiver types.
- Template parameters.
- Retry/idempotency key.
- Audit and delivery-status persistence.
- Failure policy: retry, queue, compensate, or surface synchronously.

Do not spread provider credentials or channel-specific request objects through business code.

## Verification

Test routing to the expected channel, invalid receiver handling, missing template parameters, provider errors, duplicate requests, timeout/retry behavior, secret redaction, and a sandbox/provider smoke delivery where possible.
