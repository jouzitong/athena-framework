athena:
  datasource:
    dynamic:
      enabled: true
      strict: true
      primary: master
      strategy-order: operation,annotation,tenant,readwrite
      datasources:
        master:
          url: ${MASTER_DB_URL}
          username: ${MASTER_DB_USERNAME}
          password: ${MASTER_DB_PASSWORD}
          driver-class-name: ${DB_DRIVER:com.mysql.cj.jdbc.Driver}
