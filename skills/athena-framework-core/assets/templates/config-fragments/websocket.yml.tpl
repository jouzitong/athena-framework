athena:
  websocket:
    endpoint: /ws/v1
    max-connections: 50000
    max-message-bytes: 1048576
    max-frame-bytes: 1048576
    inbound-msg-rate-limit-per-conn: 50
    handshake-rate-limit: 100
    idle-timeout-ms: 60000
    heartbeat-interval-ms: 20000
    max-outbound-queue-per-conn: 1000
    slow-conn-disconnect-threshold: 5000
    topic-throttle-default-ms: 200
    resume-ttl-ms: 300000
    backpressure-strategy: DROP_OLD
