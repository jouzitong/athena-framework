athena:
  security:
    enabled: true
    auth:
      enabled: true
      require-token: true
      token-header: Authorization
      token-prefix: Bearer
      ignore-urls:
        - /auth/login
        - /actuator/health
    token:
      type: jwt
      jwt:
        enabled: true
        secret: ${ATHENA_JWT_SECRET}
    authorization:
      enabled: false
    user:
      jpa:
        enabled: false
      mybatis:
        enabled: false
