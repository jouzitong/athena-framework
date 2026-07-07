# MyBatis Starter 快速开始

本文面向业务开发者，目标是帮助新模块快速接入 `athena-framework-starter-data-mybatis`，并理解这个 starter 当前已经提供的核心能力。

## 1. Starter 做了什么

`athena-framework-starter-data-mybatis` 是 Athena Data 模块的 MyBatis/MyBatis-Plus 实现层。它依赖 `athena-framework-starter-data-jdbc` 中的公共契约，并在启动时通过 `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` 加载 `org.athena.framework.data.mybatis.MybatisAutoConfig`。

核心能力：

- 自动扫描 `org.athena.framework.data.mybatis` 下的组件。
- 提供 MyBatis-Plus 风格的实体基类、Mapper 基类和 Service 基类。
- 提供基于 `BaseRequest` 的通用查询条件构造。
- 提供插入/更新时的审计字段填充组件。
- 提供 `IEnum`、`DBJson` 类型处理器。
- 提供实体元数据注册能力，用于根据实体解析表、字段、索引等元信息。
- 提供 `@Embedded` 对象的 SQL 展平重写能力。
- 聚合 MyBatis、MyBatis-Plus、PageHelper、Reflections 等依赖。

注意：数据库驱动在 starter 中是 `provided` 依赖，业务启动模块需要自行引入实际使用的驱动，例如 `mysql-connector-java` 或 `postgresql`。

## 2. 引入依赖

业务模块引入：

```xml
<dependency>
  <groupId>org.athena</groupId>
  <artifactId>athena-framework-starter-data-mybatis</artifactId>
</dependency>
```

启动模块按实际数据库补驱动：

```xml
<dependency>
  <groupId>mysql</groupId>
  <artifactId>mysql-connector-java</artifactId>
</dependency>
```

如果 Mapper 不在 Spring Boot 主类同包或子包下，建议在启动类上显式声明：

```java
@SpringBootApplication
@MapperScan("com.example.demo")
public class DemoApplication {
}
```

## 3. 推荐配置

最小数据源配置示例：

```yaml
spring:
  datasource:
    url: jdbc:mysql://127.0.0.1:3306/demo?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: root
    password: root
    driver-class-name: com.mysql.cj.jdbc.Driver

mybatis-plus:
  configuration:
    default-enum-type-handler: org.athena.framework.data.mybatis.handler.DefaultEnumTypeHandler
```

Data 模块通用配置：

```yaml
lib:
  jdbc:
    # 实体扫描包，实体元数据注册会按这个包扫描 IEntity 子类
    base-entity-packages:
      - com.example.demo
    # 当前默认 true，但 DDL 执行引擎不是默认 Spring Bean，见“DDL 与实体元数据”
    enable-create-table-ddl: false
    # 兼容旧配置：开启后等价于允许自动新增字段；下一个大版本移除
    auto-update-table: false
    # 字段级自动变更开关：默认只允许新增字段
    auto-add-column: true
    auto-update-column: false
    auto-drop-column: false
    # DDL 文件输出根目录
    table-ddl-path-file: config
```

## 4. 最小接入示例

### 4.1 Entity

实体优先按业务需要选择基类：

- `BaseEntity`：只有 `id`、`version`。
- `AuditableEntity`：在 `BaseEntity` 基础上增加 `create_time`、`created_by`、`update_time`、`updated_by`。
- `LogicalDeleteEntity`：在 `AuditableEntity` 基础上增加 `deleted`，并使用 MyBatis-Plus `@TableLogic`。

常见业务表建议从 `AuditableEntity` 开始：

```java
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.athena.framework.data.jdbc.annotations.JdbcColumn;
import org.athena.framework.data.mybatis.entity.AuditableEntity;

@Getter
@Setter
@TableName("demo_user")
@Table(name = "demo_user")
public class DemoUserEntity extends AuditableEntity {

    @JdbcColumn(name = "name", dataType = "VARCHAR(64)", nullable = false, comment = "用户名")
    private String name;

    @JdbcColumn(name = "status", dataType = "INT", defaultValue = "0", comment = "用户状态")
    private UserStatus status;
}
```

如果业务需要软删除：

```java
public class DemoUserEntity extends LogicalDeleteEntity {
}
```

### 4.2 DTO

```java
import lombok.Getter;
import lombok.Setter;
import org.athena.framework.data.mybatis.entity.dto.BaseDTO;

@Getter
@Setter
public class DemoUserDTO extends BaseDTO {
    private String name;
    private UserStatus status;
}
```

### 4.3 Mapper

```java
import org.apache.ibatis.annotations.Mapper;
import org.athena.framework.data.mybatis.mapper.CrudMapper;

@Mapper
public interface DemoUserMapper extends CrudMapper<DemoUserEntity> {
}
```

`CrudMapper<Entity>` 直接继承 MyBatis-Plus `BaseMapper<Entity>`，因此默认具备 `insert`、`selectById`、`updateById`、`deleteById` 等基础能力。

### 4.4 Convert

`BaseMapperService` 通过 `IConvert<Entity, DTO>` 做实体和 DTO 转换，通常用 MapStruct：

```java
import org.athena.framework.data.jdbc.convert.IConvert;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DemoUserConvert extends IConvert<DemoUserEntity, DemoUserDTO> {
}
```

`IConvert` 已约定两种更新语义：

- `updateEntityFromDto`：覆盖式更新，DTO 里的 `null` 会写入实体。
- `editEntityFromDto`：补丁式更新，DTO 里的 `null` 会被忽略。

### 4.5 Service

```java
import jakarta.annotation.Resource;
import org.athena.framework.data.jdbc.convert.IConvert;
import org.athena.framework.data.mybatis.service.BaseMapperService;
import org.springframework.stereotype.Service;

@Service
public class DemoUserService extends BaseMapperService<DemoUserEntity, DemoUserMapper, DemoUserDTO> {

    @Resource
    private DemoUserConvert convert;

    @Override
    protected IConvert<DemoUserEntity, DemoUserDTO> convert() {
        return convert;
    }
}
```

`BaseMapperService` 提供的常用方法：

- `queryAll(query)`：列表查询。
- `page(query)`：分页查询。
- `count(query)`：计数。
- `add(dto)`：新增。
- `update(id, dto)`：全量更新，允许把字段更新为 `null`。
- `edit(id, dto)`：部分更新，忽略 `null`。
- `batchAdd(dtos)` / `batchUpdate(dtos)`：批量新增/更新。
- `saveOrUpdate(dto)`：按 MyBatis-Plus 规则保存或更新。
- `get(id)`：按主键查询。
- `delete(id)`：调用 `removeById`，如果实体继承 `LogicalDeleteEntity`，会走 MyBatis-Plus 逻辑删除。

### 4.6 Query

```java
import lombok.Getter;
import lombok.Setter;
import org.athena.framework.data.jdbc.req.BaseRequest;
import org.athena.framework.data.mybatis.annotations.IgnoredQuery;

@Getter
@Setter
public class DemoUserQuery extends BaseRequest {

    private String name;

    @IgnoredQuery
    private String keyword;
}
```

默认查询构造规则来自 `MybatisPlusWrapperUtils.simpleQuery(query)`：

- `BaseRequest.sorts` 会转换为 `orderByAsc/orderByDesc`。
- Query 子类里的非空字段会按字段名转下划线后做 `eq` 查询，例如 `userName` -> `user_name = ?`。
- 标注 `@IgnoredQuery` 的字段不会参与自动查询。
- `BaseRequest.filedQueries` 支持 `EQ`、`NE`、`GT`、`GE`、`LT`、`LE`、`LIKE`、`IN`、`NOT_IN`、`IS_NULL`、`IS_NOT_NULL`。

如果某个查询需要复杂条件，直接在业务 Service 中覆盖 `buildQuery`：

```java
@Override
protected QueryWrapper<DemoUserEntity> buildQuery(DemoUserQuery query) {
    QueryWrapper<DemoUserEntity> wrapper = super.buildQuery(query);
    if (StringUtils.hasText(query.getKeyword())) {
        wrapper.and(w -> w.like("name", query.getKeyword()));
    }
    return wrapper;
}
```

### 4.7 Controller

MyBatis starter 自身不提供单独的 Controller 基类。通用 Controller 在 JDBC starter 中：

```java
import jakarta.annotation.Resource;
import org.athena.framework.data.jdbc.web.BaseController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/demo/users")
public class DemoUserController extends BaseController<DemoUserDTO, DemoUserQuery, DemoUserService> {

    @Resource
    private DemoUserService service;

    @Override
    protected DemoUserService service() {
        return service;
    }
}
```

默认接口：

- `POST /api/demo/users`
- `PUT /api/demo/users/{id}`
- `PATCH /api/demo/users/{id}`
- `DELETE /api/demo/users/{id}`
- `DELETE /api/demo/users/physical/{id}`，默认不支持，除非 Service 覆盖。
- `POST /api/demo/users/_search`
- `GET /api/demo/users/{id}`

## 5. 自动填充字段

`DataOperationHandler` 是 MyBatis-Plus `MetaObjectHandler`：

- 插入时填充 `createTime`、`updateTime`、`createdBy`、`updatedBy`、`deleted`。
- 更新时填充 `updateTime`、`updatedBy`。
- 当前用户优先来自可选的 `IUserContextService`，没有时使用 `SystemContext.DEFAULT_OPERATOR`。

自动填充生效条件：

- starter 自动配置已经加载，`MybatisAutoConfig` 扫描到了 `DataOperationHandler`。
- 实体字段带有 MyBatis-Plus `@TableField(fill = FieldFill.INSERT)` 或 `FieldFill.INSERT_UPDATE`。
- 实际执行的是 MyBatis-Plus 的插入或更新流程。

注意：`BaseEntity.version` 当前只有普通 `@TableField("version")`，`@Version` 是注释状态；如果业务要启用 MyBatis-Plus 乐观锁，需要实体字段和启动模块的 `MybatisPlusInterceptor + OptimisticLockerInnerInterceptor` 同时配置好。

## 6. 枚举和 JSON 字段

### 6.1 IEnum

`DefaultEnumTypeHandler` 会把实现 `org.arthena.framework.common.enums.IEnum` 的枚举按 `getCode()` 存成数据库 `INT`。

推荐在应用配置中显式指定：

```yaml
mybatis-plus:
  configuration:
    default-enum-type-handler: org.athena.framework.data.mybatis.handler.DefaultEnumTypeHandler
```

表字段建议使用 `INT`，不要按枚举名设计成 `VARCHAR`。

### 6.2 DBJson

`DefaultJsonHandler` 面向 `DBJson`，以 `VARCHAR` 存储 JSON 字符串，读写时通过 Jackson 转换。字段使用时按 MyBatis 类型处理器的常规方式配置即可。

## 7. DDL 与实体元数据

`DefaultEntityMetadataRegistry` 是默认启用的组件，启动时会根据 `lib.jdbc.base-entity-packages` 扫描 `IEntity` 子类，并解析为 `TableMeta`。

解析规则主要来自：

- `TableInfoParser`：读取 `@Table` 或 `@TableName` 的表名。
- `ColumnMetaParser`：优先读取字段上的 `@JdbcColumn`，也兼容 `jakarta.persistence.Column`、MyBatis-Plus `@TableField`、`@TableId`，并支持 `@Embedded` 字段的递归解析。
- `IndexMetaParser`：解析索引元数据，`@Id` 和 `@TableId` 都会生成主键索引。

字段类型推断规则：

- `@JdbcColumn` 可一次性声明列名、DDL 类型、长度、精度、可空、自增、唯一、默认值、注释。
- 主键仍通过 `@Id` 或 `@TableId` 声明；`@JdbcColumn` 不负责主键标记。
- `@Column(columnDefinition = "...")` 会作为显式 DDL 类型优先使用。
- `String` 默认生成 `VARCHAR(255)`。
- 实现 `IEnum` 或普通 Java `enum` 的字段默认生成 `INT`。
- 可使用 `@DdlColumnLength(128)` 覆盖字符串等带长度类型的生成长度。
- 可在实体类上使用 `@DdlIgnoreTable` 跳过 DDL 表生成；框架内置的 `BaseEntity`、`AuditableEntity`、`LogicalDeleteEntity` 已标记该注解。

`DefaultGenerateDdlEngine` 当前类上 `@Service` 是注释状态，所以不是默认 Spring Bean。它代表一个可复用的 DDL 生成执行引擎：

- 扫描 `lib.jdbc.base-entity-packages` 下的 `IEntity` 子类。
- 基于实体元数据生成 MySQL 建表 SQL。
- 输出到 `lib.jdbc.table-ddl-path-file/${spring.application.name}/${common.version}/create_table_ddl.sql`。
- `update_table_ddl.sql` 会读取当前数据库表字段，按 `auto-add-column` / `auto-update-column` / `auto-drop-column` 控制字段级变更；默认只生成新增字段 `ADD COLUMN`，字段类型变化暂不处理。
- `auto-update-table` 是兼容旧配置的总开关，当前 `true` 等价于允许 `auto-add-column=true`，下一个大版本会移除。
- 当 `lib.jdbc.auto-update-table=true` 时尝试执行建表 SQL。

因此，快速开发时可以先手写初始化 SQL；如果要使用自动 DDL 能力，需要在业务侧显式注册或封装该引擎，并谨慎开启生产环境自动更新表。

## 8. Embedded 对象

starter 提供 `EmbeddedInterceptor` 和 `EmbeddedSqlBuilder`，用于处理实体字段上的 `jakarta.persistence.Embedded`：

- 当参数对象中存在 `@Embedded` 字段时，拦截 MyBatis `StatementHandler.prepare`。
- 解析嵌入对象的子字段。
- 根据字段前缀把嵌入字段展平成数据库列。
- 对 `INSERT`、`UPDATE`、`SELECT`、`DELETE` SQL 做重写。

这个能力适合值对象字段需要落到同一张表多个列的场景。使用前建议先写一条最小 Mapper 用例验证生成 SQL，因为它依赖 SQL 解析和参数重写，复杂手写 SQL 需要单独确认。

## 9. 常见坑

- Mapper 没被注入：优先检查启动类包路径和 `@MapperScan`，不要只依赖 `scanBasePackages`。
- 枚举字段读写异常：确认是否配置了 `mybatis-plus.configuration.default-enum-type-handler`，并确认数据库字段是 `INT`。
- 审计字段没填充：检查实体字段的 `@TableField(fill = ...)`，以及是否走 MyBatis-Plus 的插入/更新路径。
- `updatedBy` 没更新：当前填充逻辑使用公共常量写入字段名，遇到字段命名不一致时要以实际实体和常量为准做验证。
- 乐观锁报 `MP_OPTLOCK_VERSION_ORIGINAL`：确认是否真的启用了 `@Version`，以及启动模块是否注册 `OptimisticLockerInnerInterceptor`。
- 通用查询误查 `deleted`：当前 `simpleQuery()` 没有默认追加 `deleted = 0`；软删除过滤交给 MyBatis-Plus 的 `@TableLogic`。
- DDL 没生成：`DefaultGenerateDdlEngine` 当前不是默认 Bean，只有实体元数据注册默认启用。
- 物理删除不可用：`BaseController` 暴露了 `/physical/{id}`，但 `IMapperService.physicalDelete` 默认抛 `NotSupportException`，需要业务 Service 自行覆盖。

## 10. 快速接入检查清单

1. 引入 `athena-framework-starter-data-mybatis`。
2. 启动模块引入数据库驱动。
3. 配置 `spring.datasource`。
4. 配置 `@MapperScan` 或确保 Mapper 在默认扫描范围内。
5. 配置 `lib.jdbc.base-entity-packages`。
6. 如使用 `IEnum`，配置 `default-enum-type-handler`。
7. Entity 选择 `BaseEntity` / `AuditableEntity` / `LogicalDeleteEntity`。
8. Mapper 继承 `CrudMapper<Entity>`。
9. Convert 实现 `IConvert<Entity, DTO>`。
10. Service 继承 `BaseMapperService<Entity, Mapper, DTO>`。
11. Controller 如需复用标准 CRUD，可继承 JDBC 层 `BaseController<DTO, Query, Service>`。
