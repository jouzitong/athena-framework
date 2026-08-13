# Athena MyBatis CRUD 参考实现

这份示例以 `Order` 为业务对象，展示消费项目中一条完整的 MyBatis CRUD 链路。它对应当前快照中的 `athena-framework-starter-data-mybatis`，不是 Athena 内部源码的复制品。

## 目录结构

```text
com.example.order
├── model
│   ├── OrderDTO.java
│   └── OrderQuery.java
├── persistence
│   ├── OrderEntity.java
│   └── OrderMapper.java
├── service
│   ├── OrderConvert.java
│   └── OrderService.java
├── web
│   └── OrderController.java
└── resources
    └── application.yml
```

## 1. 依赖与配置

消费项目至少直接声明 MyBatis、Web 和实际数据库驱动。数据库驱动不要依赖 starter 的 `provided` 声明：

```xml
<dependency>
    <groupId>org.athena</groupId>
    <artifactId>athena-framework-starter-data-mybatis</artifactId>
</dependency>
<dependency>
    <groupId>org.athena</groupId>
    <artifactId>athena-framework-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <scope>runtime</scope>
</dependency>
```

```yaml
spring:
  datasource:
    url: ${DB_URL:jdbc:mysql://localhost:3306/order_db?useUnicode=true&characterEncoding=utf8&serverTimezone=UTC}
    username: ${DB_USERNAME:order_app}
    password: ${DB_PASSWORD:}
    driver-class-name: com.mysql.cj.jdbc.Driver

lib:
  jdbc:
    type: MYSQL
    # 生产环境不要让实体扫描自动改变数据库结构。
    enable-create-table-ddl: false
    auto-add-column: false
    auto-update-column: false
    auto-drop-column: false
    base-entity-packages:
      - com.example.order.persistence
```

如果项目没有统一的 `@MapperScan`，在启动类或配置类中扫描消费项目自己的 mapper：

```java
@SpringBootApplication
@MapperScan("com.example.order.persistence")
public class OrderApplication {
}
```

## 2. Entity：数据库映射对象

```java
package com.example.order.persistence;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import org.athena.framework.data.mybatis.entity.BaseEntity;

@TableName("biz_order")
public class OrderEntity extends BaseEntity {

    @TableField("order_no")
    private String orderNo;

    @TableField("customer_id")
    private Long customerId;

    @TableField("status")
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

`BaseEntity` 已提供 `id` 和 `version`。需要审计字段时改为继承 `org.athena.framework.data.mybatis.entity.AuditableEntity`；需要逻辑删除时改为继承 `LogicalDeleteEntity`，不要只在业务表中增加一个名为 `deleted` 的字段就宣称已接入软删除。

## 3. DTO：接口输入输出对象

```java
package com.example.order.model;

import org.athena.framework.data.mybatis.entity.dto.BaseDTO;

public class OrderDTO extends BaseDTO {

    private String orderNo;
    private Long customerId;
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

不要直接把 Entity 作为 Controller 入参。DTO 可以按场景拆成 `OrderCreateRequest`、`OrderUpdateRequest`、`OrderView`；若复用一个 DTO，至少明确 `id`、`version`、创建人和审计字段是否允许客户端写入。

## 4. Query：查询条件对象

```java
package com.example.order.model;

import org.athena.framework.data.jdbc.req.BaseRequest;

public class OrderQuery extends BaseRequest {

    private String status;
    private Long customerId;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }
}
```

当前 `MybatisPlusWrapperUtils` 会把 Query 子类中非空字段按等值条件加入查询，并处理 `BaseRequest` 的 `sorts`、`filedQueries`。因此不要把任意客户端字段直接传入生产接口。对 `filedQueries` 和 `sorts` 建立允许列表，例如只允许 `orderNo`、`customerId`、`status`，并把内部字段标记为 `@IgnoredQuery` 或在服务层拒绝。

## 5. Mapper：持久化入口

```java
package com.example.order.persistence;

import org.apache.ibatis.annotations.Mapper;
import org.athena.framework.data.mybatis.mapper.CrudMapper;

@Mapper
public interface OrderMapper extends CrudMapper<OrderEntity> {
}
```

项目已经使用 `@MapperScan` 时可以省略 `@Mapper`，但必须确认扫描路径覆盖该接口。只有需要自定义 SQL 时才在此增加方法和 XML/注解 SQL，并对动态 SQL 参数做白名单约束。

## 6. Convert：Entity 与 DTO 转换

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
        if (entity == null) {
            return null;
        }
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
        if (dto == null) {
            return null;
        }
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
        if (dto.getOrderNo() != null) {
            entity.setOrderNo(dto.getOrderNo());
        }
        if (dto.getCustomerId() != null) {
            entity.setCustomerId(dto.getCustomerId());
        }
        if (dto.getStatus() != null) {
            entity.setStatus(dto.getStatus());
        }
    }

    @Override
    public void updateEntityFromDto(OrderDTO dto, OrderEntity entity) {
        entity.setOrderNo(dto.getOrderNo());
        entity.setCustomerId(dto.getCustomerId());
        entity.setStatus(dto.getStatus());
    }
}
```

这里体现 Athena 的关键语义：`PATCH` 调用 `editEntityFromDto`，忽略 null；`PUT` 调用 `updateEntityFromDto`，允许 null 覆盖。主键不应被更新，版本号应由并发控制策略管理。

## 7. Service：CRUD 编排与业务边界

```java
package com.example.order.service;

import com.example.order.model.OrderDTO;
import com.example.order.persistence.OrderEntity;
import com.example.order.persistence.OrderMapper;
import org.athena.framework.data.jdbc.convert.IConvert;
import org.athena.framework.data.mybatis.service.BaseMapperService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService extends BaseMapperService<OrderEntity, OrderMapper, OrderDTO> {

    private final OrderConvert convert;

    public OrderService(OrderConvert convert) {
        this.convert = convert;
    }

    @Override
    protected IConvert<OrderEntity, OrderDTO> convert() {
        return convert;
    }

    @Transactional
    public OrderDTO createOrder(OrderDTO dto) {
        // 在这里校验订单号唯一性、库存、租户和状态，而不是放进 Controller。
        return add(dto);
    }
}
```

当前 MyBatis `BaseMapperService` 已实现 `queryAll`、`page`、`count`、`add`、`update`、`edit`、`get`、`delete` 等通用操作。对有业务约束的写操作，在消费方 Service 增加明确方法和事务；不要把所有业务都暴露成无约束通用 CRUD。

## 8. Controller：HTTP 边界

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

当前公共 Controller 契约提供：`POST /api/orders`、`PUT/PATCH /api/orders/{id}`、`DELETE /api/orders/{id}`、`DELETE /api/orders/physical/{id}`、`GET /api/orders/{id}`、兼容 GET 分页和 `POST /api/orders/_search`。确认删除语义和安全授权后再暴露物理删除。

## 9. 验收重点

- 验证 mapper 扫描、数据库驱动和分页插件真实加载。
- 验证 Query 字段、`filedQueries`、排序字段和操作符允许列表。
- 验证 PUT 覆盖 null、PATCH 忽略 null、version 并发更新和删除语义。
- 验证事务回滚、唯一索引、迁移脚本和目标数据库方言。
- 先运行项目内 `./.codex/skills/athena-framework-core/scripts/validate_project.py`，再运行消费项目的单元/集成测试和 Maven 验证。
