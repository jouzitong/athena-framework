spring:
  application:
    name: {{ARTIFACT_ID}}
  cloud:
    nacos:
      discovery:
        server-addr: ${NACOS_SERVER_ADDR}
        namespace: ${NACOS_NAMESPACE:}
        group: ${NACOS_GROUP:DEFAULT_GROUP}
        username: ${NACOS_USERNAME:}
        password: ${NACOS_PASSWORD:}
      config:
        server-addr: ${spring.cloud.nacos.discovery.server-addr}
        namespace: ${spring.cloud.nacos.discovery.namespace}
        group: ${spring.cloud.nacos.discovery.group}
        file-extension: yml
