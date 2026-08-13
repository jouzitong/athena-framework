athena:
  kafka:
    enabled: true
    bootstrap-servers: ${KAFKA_BOOTSTRAP_SERVERS}
    client-id: {{ARTIFACT_ID}}
    producer-acks: all
    producer-retries: 3
    consumer-group-id: ${KAFKA_CONSUMER_GROUP:{{ARTIFACT_ID}}}
    consumer-auto-offset-reset: latest
    consumer-enable-auto-commit: false
    listener-concurrency: 1
