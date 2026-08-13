spring:
  application:
    name: {{ARTIFACT_ID}}

server:
  port: 8080

management:
  endpoints:
    web:
      exposure:
        include: health,info
