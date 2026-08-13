spring:
  application:
    name: {{ARTIFACT_ID}}

logging:
  level:
    root: INFO

athena:
  log:
    dir: ${LOG_DIR:./logs}
    rolling:
      max-history: 30
      total-size-cap: 10GB
      max-file-size: 256MB
