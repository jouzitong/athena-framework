# Athena Framework Skills

这组目录是面向 Athena Framework 消费项目的项目级 Codex Skill 分发源。它们不是 Maven 依赖，也不会把业务代码安装进消费项目；安装后由项目内的 `.codex/skills` 提供开发工作流，并根据项目实际 POM、源码、配置和测试生成消费方代码。

## 目录职责

| Skill | 用途 |
| --- | --- |
| `athena-framework-core` | 共享版本快照、模块状态、依赖推荐、脚手架、静态校验和 Maven 验证 |
| `athena-web` | Web MVC、HTTP API、DTO、错误处理和接口验收 |
| `athena-data` | MyBatis、JPA、Mongo、JDBC、动态数据源和事务 |
| `athena-security` | Token、JWT、用户存储、RBAC、权限、网关和审计 |
| `athena-cloud` | Nacos、OpenFeign、Seata 和服务边界 |
| `athena-messaging` | Kafka、邮件、短信、企业微信和消息可靠性 |
| `athena-realtime` | WebSocket、会话、路由、ACL、恢复和集群扩展点 |
| `athena-storage-observability` | MinIO、Elasticsearch、日志和运行观测 |
| `athena-testing` | 分层测试、静态校验、验收证据和外部服务边界 |

所有领域 Skill 依赖项目内的 `athena-framework-core`。`registry.json` 是项目级安装依赖关系的唯一入口；这些依赖不会解析开发者机器上的 `$CODEX_HOME/skills`。

## 安装到消费项目

在 Athena Framework 仓库中执行，指定消费项目根目录：

```bash
python3 skills/install_skills.py --list
python3 skills/install_skills.py --project /path/to/consumer-project --skill athena-data
python3 skills/install_skills.py --project /path/to/consumer-project --all
```

默认目标是 `/path/to/consumer-project/.codex/skills`。指定 `--target` 可安装到临时目录做验证；已有目标需要 `--force`，脚本会先创建时间戳备份。不要把它安装到开发者个人的 `~/.codex/skills`，项目 Skill 的版本应该随项目一起管理。

正式接入后，将消费项目的 `.codex/skills` 纳入版本控制并提交。这样团队成员、CI 和新环境使用的是同一份项目 Skill；升级时重新执行安装器，审查差异后再提交。`--force` 生成的备份放在 `.codex/skill-backups`，不属于 Skill 发现目录，确认无须保留后可删除。

安装后，Codex 从消费项目的 `.codex/skills` 发现 `$athena-data`、`$athena-web` 等入口；核心共享 Skill 也位于同一个项目目录中。

## 版本边界

核心 Skill 的 `references/framework-manifest.json` 来自当前 Athena checkout。若消费项目使用其他版本，必须以其解析后的 Maven 依赖和匹配版本源码/JAR 为准，并把版本差异作为验证结果报告出来。
