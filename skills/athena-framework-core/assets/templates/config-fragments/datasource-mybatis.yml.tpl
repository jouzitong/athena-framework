spring:
  datasource:
    url: ${DB_URL}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
    driver-class-name: ${DB_DRIVER:com.mysql.cj.jdbc.Driver}

lib:
  jdbc:
    type: ${DB_TYPE:MYSQL}
    enable-event: true
    enable-create-table-ddl: false
    auto-add-column: false
    auto-update-column: false
    auto-drop-column: false
    base-entity-packages:
      - {{PACKAGE}}.persistence
