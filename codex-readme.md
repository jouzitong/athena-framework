# Athena Framework - Codex Readme

## 项目定位与整体结构
Athena Framework 是一个面向 Java 业务系统的基础能力框架，以 Maven 多模块的方式组织。它不提供业务逻辑，而是沉淀通用工程能力、约定与技术规范，作为业务系统的底座被依赖引入。

顶层 `pom.xml` 聚合了三个模块：
- `athena-project`：框架核心能力与 starter 体系
- `athena-project/framework-dependencies`：依赖与版本的 BOM 管理
- `athena-framework-test`：框架能力验证与示例工程

## 代码模块理解
### 1) framework-dependencies (BOM)
集中管理 Spring Boot / Spring Cloud / Alibaba 版本，以及数据库、MyBatis、Druid、工具库等依赖版本。通过 `revision` 统一控制框架版本号，业务系统可在 `dependencyManagement` 中 import 该 BOM。

### 2) athena-project
是框架核心，包含：
- `athena-framework-common`：基础能力与通用抽象
  - 基础异常体系、通用常量/枚举、上下文与事件发布、通用工具类（对象/日期/配置/反射等）
  - 线程/锁相关基础服务
- `athena-framework-starters`：Spring Boot 自动装配入口
  - `athena-framework-starter-web`：Web 侧统一返回、统一异常、注解封装、日期处理等
  - `athena-framework-starter-data`：数据访问抽象
    - `data-jdbc`：基础请求/分页模型、通用 controller / service 抽象、字段类型与查询模型
    - `data-jpa`：基础实体、审计、逻辑删除、Repository 抽象
    - `data-mybatis`：DDL 生成、SQL 重写、实体元信息、通用 Mapper 等
  - `athena-framework-starter-es`：ES 相关基础配置
  - `athena-framework-starter-nacos`：Nacos 配置基础
  - `athena-framework-starter-seata`：分布式事务相关配置
  - `athena-framework-data-starter-mongo`：MongoDB 相关适配
  - `athena-framework-starters-ai`：AI 相关 starter 占位/扩展

### 3) athena-framework-test
以多个独立测试/示例工程验证框架能力：
- MyBatis/JPA/ES/Nacos/Seata/Log4j2 等集成示例
- 更接近“验收工程”而不是单元测试

## 构建与约定
- 使用 JDK 17 与 Maven 构建
- 通过 `revision` 统一版本号（配合 flatten-maven-plugin）
- 框架以依赖方式被业务系统使用，不需要单独运行

## 设计取向
从 README 与文档信息看，框架强调：
- 分层清晰、职责单一
- 低侵入、可选用
- 约定优于配置

## 我对代码的理解（简版）
这是一个以 Spring Boot 生态为基础的“工程能力沉淀层”，通过 `common` 模块提供最小基础抽象，通过 `starter` 体系提供可插拔的自动装配能力，把日常项目中重复的 Web 规范、数据访问抽象、通用工具与异常模型、基础配置整合成可复用组件。`framework-dependencies` 负责稳定版本基线，`framework-test` 负责用可运行工程验证组合能力。
