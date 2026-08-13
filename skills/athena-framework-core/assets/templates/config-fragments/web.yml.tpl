lib:
  web:
    enum-packages:
      - {{PACKAGE}}
    sign:
      enabled: false
      key-id: default
      secret: ${ATHENA_WEB_SIGN_SECRET:}
