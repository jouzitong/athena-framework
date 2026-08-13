# MongoDB 数据开发参考实现

当前 `athena-framework-starter-data-mongo` 是依赖聚合入口：它引入 `spring-boot-starter-data-mongodb`，但没有 Athena Mongo Entity、Mapper、Service 或 Controller 基类。因此 Mongo 代码必须由消费项目自己实现，不能套用 MyBatis/JPA 的 `BaseMapperService` 链路。

## 目录结构

```text
com.example.order
├── model
│   └── OrderView.java
├── persistence
│   ├── OrderDocument.java
│   └── OrderRepository.java
├── service
│   └── OrderService.java
└── web
    └── OrderController.java
```

## 1. 依赖与配置

```xml
<dependency>
    <groupId>org.athena</groupId>
    <artifactId>athena-framework-starter-data-mongo</artifactId>
</dependency>
<dependency>
    <groupId>org.athena</groupId>
    <artifactId>athena-framework-starter-web</artifactId>
</dependency>
```

Mongo starter 当前已带入 Spring Data Mongo 依赖。消费项目仍需提供连接配置和生产凭据：

```yaml
spring:
  data:
    mongodb:
      uri: ${MONGODB_URI:mongodb://localhost:27017/order_db}
```

生产环境还应明确 TLS、连接池、超时、读写关注级别、索引创建策略和凭据来源。不要让应用启动时无审查地自动创建或删除索引。

## 2. Document：Mongo 文档对象

```java
package com.example.order.persistence;

import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "biz_order")
public class OrderDocument {

    @Id
    private String id;

    @Field("order_no")
    private String orderNo;

    @Field("customer_id")
    private Long customerId;

    private String status;
    private Instant createdAt;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
```

Mongo 的标识、版本、审计和软删除语义不由 Athena JDBC `BaseEntity` 自动提供。需要乐观锁时设计 Mongo 的 `@Version`/更新条件；需要租户隔离时把租户字段纳入每个查询和唯一索引，不能只依赖 Controller 参数。

## 3. View：接口输出对象

```java
package com.example.order.model;

public record OrderView(
        String id,
        String orderNo,
        Long customerId,
        String status) {
}
```

Mongo 示例不复用 Athena `BaseDTO`，因为 Mongo starter 没有统一 JDBC CRUD 合约。创建和更新请求可以继续单独定义 `CreateOrderRequest`、`PatchOrderRequest`，避免暴露文档内部字段。

## 4. Repository：Spring Data Mongo 接口

```java
package com.example.order.persistence;

import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrderRepository extends MongoRepository<OrderDocument, String> {

    Optional<OrderDocument> findByOrderNo(String orderNo);

    List<OrderDocument> findByCustomerIdAndStatus(Long customerId, String status);
}
```

简单条件可以使用派生查询；多条件、字段投影、分页、聚合和租户条件建议由 Service 调用 `MongoTemplate` 明确构造，并对排序字段和查询字段做允许列表。

## 5. Service：领域操作与事务边界

```java
package com.example.order.service;

import com.example.order.model.OrderView;
import com.example.order.persistence.OrderDocument;
import com.example.order.persistence.OrderRepository;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private final OrderRepository repository;

    public OrderService(OrderRepository repository) {
        this.repository = repository;
    }

    public OrderView get(String id) {
        return repository.findById(id).map(this::toView).orElse(null);
    }

    public OrderView create(String orderNo, Long customerId) {
        if (repository.findByOrderNo(orderNo).isPresent()) {
            throw new IllegalArgumentException("orderNo already exists");
        }
        OrderDocument document = new OrderDocument();
        document.setOrderNo(orderNo);
        document.setCustomerId(customerId);
        document.setStatus("NEW");
        return toView(repository.save(document));
    }

    private OrderView toView(OrderDocument document) {
        return new OrderView(document.getId(), document.getOrderNo(),
                document.getCustomerId(), document.getStatus());
    }
}
```

不要默认把 Mongo 操作标记成与关系数据库相同的事务语义。跨文档/跨服务一致性需要根据 Mongo 事务、写关注和消息/outbox 设计单独验证。

## 6. Controller：消费方自定义 HTTP 合约

```java
package com.example.order.web;

import com.example.order.model.OrderView;
import com.example.order.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mongo-orders")
public class OrderController {

    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public OrderView get(@PathVariable String id) {
        return service.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderView create(@RequestParam String orderNo,
                            @RequestParam Long customerId) {
        return service.create(orderNo, customerId);
    }
}
```

生产项目应将 `@RequestParam` 示例替换为请求 DTO，加入校验、统一错误响应、认证授权和幂等键。若需要统一分页响应，消费项目自己定义 Mongo 分页 DTO 或适配 Athena Web 的公共响应类型；不要伪装成 Athena JDBC `BaseController`。

## 7. 验收重点

- 验证 `@Document` collection、字段映射、唯一索引、连接超时和读写关注。
- 验证租户过滤、查询字段/排序白名单、分页稳定排序和大结果集限制。
- 验证重复写、并发更新、重试、部分失败、文档迁移和过期数据清理。
- 明确 Mongo 事务、关系数据库事务和消息一致性之间的边界。
- 运行消费方 Mongo 集成测试；只有编译通过时，不能声称 Mongo CRUD 已验证。
