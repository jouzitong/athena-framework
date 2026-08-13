athena:
  minio:
    enabled: true
    endpoint: ${ATHENA_MINIO_ENDPOINT}
    access-key: ${ATHENA_MINIO_ACCESS_KEY}
    secret-key: ${ATHENA_MINIO_SECRET_KEY}
    bucket: ${ATHENA_MINIO_BUCKET}
    auto-create-bucket: false
    presign-expiry-seconds: 900
    connect-timeout-ms: 3000
    write-timeout-ms: 10000
    read-timeout-ms: 10000
