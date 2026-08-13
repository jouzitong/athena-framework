# Storage and observability

## MinIO

`athena-framework-starter-minio` is disabled by default. Enabling it requires endpoint, access key, secret key, and bucket. It supplies `ObjectStorageService` with put/get/remove/stat/exists and presigned GET/PUT operations.

- Externalize credentials.
- Set `auto-create-bucket=false` outside controlled development unless the deployment policy permits creation.
- Validate endpoint scheme/secure behavior.
- Enforce object key, content type, size, tenant/ownership, and authorization in the consumer.
- Bound presigned URL expiry and never log the URL as harmless metadata.
- Test large/unknown-length streams and partial failures.

Do not expose `ObjectStorageService` directly from a public controller without business authorization and object-key policy.

## Elasticsearch

The ES starter is a thin implemented entry in this snapshot. Inspect the resolved POM and `EsConfig` before selecting client APIs, index management, or repository patterns. Keep index templates, mappings, aliases, retention, and reindex strategy in the consumer/platform boundary.

## Logging starter

The log starter registers an environment post-processor and bundled `logback-spring.xml` defaults:

- Console plus rolling pattern logs for dev/local/test.
- Rolling JSON logs for prod/staging.
- Application/error files, with optional SQL/audit logger patterns.
- Configurable directory, root level, retention, total size, and file size.

Verify which Logback configuration wins in the consumer. Do not ship duplicate appenders or assume the bundled file is active without inspecting startup logging.

## Observability rules

- Propagate trace/request IDs through HTTP, async work, Feign, messaging, and WebSocket where supported.
- Use structured fields for service/environment and business correlation.
- Redact credentials, tokens, personal data, message bodies, presigned URLs, and database parameters.
- Define log retention and disk limits for the real deployment filesystem.
- Add metrics/health checks for external dependencies and capacity-sensitive components.
- Distinguish a configured appender from a verified log collection/indexing pipeline.
