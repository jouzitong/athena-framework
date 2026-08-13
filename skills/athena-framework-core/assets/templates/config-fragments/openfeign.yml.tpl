athena:
  cloud:
    openfeign:
      enabled: true
      base-packages:
        - {{PACKAGE}}.client
      connect-timeout-millis: 30000
      read-timeout-millis: 60000
      logger-level: BASIC
      application-name-header: X-App-Name
