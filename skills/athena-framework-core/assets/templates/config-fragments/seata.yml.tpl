athena:
  cloud:
    seata:
      enabled: true
      data-source-proxy-mode: AT

seata:
  enabled: true
  tx-service-group: ${SEATA_TX_SERVICE_GROUP}
