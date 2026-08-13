# Athena JPA CRUD 参考实现

这份示例展示 `athena-framework-starter-data-jpa` 的消费方实现。JPA 使用 Spring Data Repository 和 Specification 查询，不使用 MyBatis Mapper；Controller 仍复用 Athena JDBC 层的公共 CRUD 契约。

## 目录结构

```text
com.example.order
├── model
│   ├── OrderDTO.java
│   └── OrderQuery.java
├── persistence
│   ├── OrderEntity.java
│   └── OrderRepository.java
├── service
│   ├── OrderConvert.java
│   └── OrderService.java
├── web
│   └── OrderController.java
└── resources
    └── application.yml
```

## 1. 依赖与配置

```xml
<dependency>
    <groupId>org.athena</groupId>
    <artifactId>athena-framework-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>org.athena</groupId>
    <artifactId>athena-framework-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
```

JPA starter 将 Web 和数据库驱动声明为 `provided`，因此消费项目需要按自己的数据库直接添加驱动。生产配置建议：

```yaml
spring:
  datasource:
    url: ${DB_URL:jdbc:postgresql://localhost:5432/order_db}
    username: ${DB_USERNAME:order_app}
    password: ${DB_PASSWORD:}
  jpa:
    hibernate:
      ddl-auto: validate
    open-in-view: false
    properties:
      hibernate:
        format_sql: false
```

不要用 `update`、`create` 或 `create-drop` 代替正式迁移；用 Flyway/Liquibase 或平台迁移流程管理表结构。

## 2. Entity：JPA 持久化对象

```java
package com.example.order.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.athena.framework.data.jpa.domain.LogicalDeleteEntity;

@Entity
@Table(name = "biz_order")
public class OrderEntity extends LogicalDeleteEntity {

    @Column(name = "order_no", nullable = false, length = 64, unique = true)
    private String orderNo;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(name = "status", nullable = false, length = 32)
    private String status;

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
```

`BaseEntity` 提供 `id` 和 JPA `@Version`；示例继承 `LogicalDeleteEntity`，当前快照使用 Hibernate `@SoftDelete`。如果业务需要真实物理删除或显式 deleted 字段，必须核对当前 Hibernate/JPA 版本和实际 DDL，不要只根据类名推断行为。

## 3. DTO 与 Query

```java
package com.example.order.model;

import org.athena.framework.data.jpa.domain.dto.BaseDTO;

public class OrderDTO extends BaseDTO {

    private String orderNo;
    private Long customerId;
    private String status;

    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
```

```java
package com.example.order.model;

import org.athena.framework.data.jdbc.req.BaseRequest;

public class OrderQuery extends BaseRequest {

    private String status;
    private Long customerId;

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
}
```

当前 `JpaQueryEngineUtils` 会将 Query 子类非空字段转为 `EQ` 条件，并将 `filedQueries` 和 `sorts` 交给 JPA Criteria。JPA 排序使用 Entity 属性名；MyBatis 排序通常转换为下划线列名，两个实现不要混用字段名规则。对客户端查询字段建立白名单，并使用 `@IgnoredQuery` 排除不应自动参与查询的内部字段。

## 4. Repository：Spring Data 持久化接口

```java
package com.example.order.persistence;

import java.util.Optional;
import org.athena.framework.data.jpa.repository.BaseRepository;

public interface OrderRepository extends BaseRepository<OrderEntity> {

    Optional<OrderEntity> findByOrderNo(String orderNo);
}
```

`BaseRepository` 已继承 `JpaRepository` 和 `JpaSpecificationExecutor`，这是 Athena JPA Service 进行分页和动态条件查询的基础。复杂查询优先在 Repository 中增加明确的 Specification/JPQL 方法，不要在 Controller 拼接 JPQL。

## 5. Convert：Entity 与 DTO 转换

```java
package com.example.order.service;

import com.example.order.model.OrderDTO;
import com.example.order.persistence.OrderEntity;
import org.athena.framework.data.jdbc.convert.IConvert;
import org.springframework.stereotype.Component;

@Component
public class OrderConvert implements IConvert<OrderEntity, OrderDTO> {

    @Override
    public OrderDTO toDTO(OrderEntity entity) {
        if (entity == null) return null;
        OrderDTO dto = new OrderDTO();
        dto.setId(entity.getId());
        dto.setVersion(entity.getVersion());
        dto.setOrderNo(entity.getOrderNo());
        dto.setCustomerId(entity.getCustomerId());
        dto.setStatus(entity.getStatus());
        return dto;
    }

    @Override
    public OrderEntity toEntity(OrderDTO dto) {
        if (dto == null) return null;
        OrderEntity entity = new OrderEntity();
        entity.setId(dto.getId());
        entity.setVersion(dto.getVersion());
        entity.setOrderNo(dto.getOrderNo());
        entity.setCustomerId(dto.getCustomerId());
        entity.setStatus(dto.getStatus());
        return entity;
    }

    @Override
    public void editEntityFromDto(OrderDTO dto, OrderEntity entity) {
        if (dto.getOrderNo() != null) entity.setOrderNo(dto.getOrderNo());
        if (dto.getCustomerId() != null) entity.setCustomerId(dto.getCustomerId());
        if (dto.getStatus() != null) entity.setStatus(dto.getStatus());
    }

    @Override
    public void updateEntityFromDto(OrderDTO dto, OrderEntity entity) {
        entity.setOrderNo(dto.getOrderNo());
        entity.setCustomerId(dto.getCustomerId());
        entity.setStatus(dto.getStatus());
    }
}
```

## 6. Service：实现 Athena JPA Service 契约

```java
package com.example.order.service;

import com.example.order.model.OrderDTO;
import com.example.order.persistence.OrderEntity;
import com.example.order.persistence.OrderRepository;
import org.athena.framework.data.jdbc.convert.IConvert;
import org.athena.framework.data.jpa.repository.BaseRepository;
import org.athena.framework.data.jpa.service.BaseMapperService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService extends BaseMapperService<OrderEntity, OrderDTO> {

    private final OrderRepository repository;
    private final OrderConvert convert;

    public OrderService(OrderRepository repository, OrderConvert convert) {
        this.repository = repository;
        this.convert = convert;
    }

    @Override
    protected Class<?> entityType() {
        return OrderEntity.class;
    }

    @Override
    protected IConvert<OrderEntity, OrderDTO> convert() {
        return convert;
    }

    @Override
    protected BaseRepository<OrderEntity> repository() {
        return repository;
    }

    @Transactional
    public OrderDTO createOrder(OrderDTO dto) {
        // 校验订单号、租户、库存和状态迁移，再调用 add。
        return add(dto);
    }
}
```

JPA Service 必须实现 `entityType()`、`convert()`、`repository()`。当前基类负责分页、Specification 查询、增删改查和拦截器钩子；业务事务、状态机、并发失败重试和领域校验仍放在消费方 Service。

## 7. Controller：复用公共 CRUD 路由

```java
package com.example.order.web;

import com.example.order.model.OrderDTO;
import com.example.order.model.OrderQuery;
import com.example.order.service.OrderService;
import org.athena.framework.data.jdbc.web.BaseController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController extends BaseController<OrderDTO, OrderQuery, OrderService> {

    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    @Override
    protected OrderService service() {
        return service;
    }
}
```

## 8. 验收重点

- 验证 Entity 扫描、Repository 扫描、数据库驱动和 `ddl-auto`。
- 验证 `@Version` 乐观锁、`LogicalDeleteEntity` 删除行为和物理删除风险。
- 验证 Entity 属性名与 `filedQueries`/`sorts` 的映射，拒绝未知字段。
- 验证 PUT/PATCH null 语义、事务回滚、懒加载边界和 `open-in-view=false` 下的序列化。
- 使用真实目标数据库或 Testcontainers 验证方言、索引、迁移和分页。
