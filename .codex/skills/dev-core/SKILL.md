# Skill: dev-core

## Purpose

将用户输入的需求（可能口语化、不完整）转化为符合项目 DSL 的可执行任务，并完成从实现到测试的交付闭环。

## When To Use

- 用户显式提到 `dev-core`
- 任务语义属于功能开发、能力改造、缺陷修复并要求可测试交付

## Input Assumptions

- 用户输入可能不完整，默认先进入“需求文档化”阶段。
- 若缺少关键约束（范围、验收、依赖），先补齐再进入开发。

## Workflow (Mandatory 5 Stages)

1. 需求文档
2. 需求原型
3. 任务划分
4. 系统开发
5. 系统测试

## DSL Alignment Rules (Mandatory)

- Task 定义必须对齐 `.codex/DSL/rules/01-task-rules.adoc`。
- 任务生成必须对齐 `.codex/DSL/rules/01-task-gen-rules.adoc` 与 `.codex/DSL/flows/gen/*`。
- 任务执行必须对齐 `.codex/DSL/flows/task-execution-flow.adoc` 的 5 步：
  - ANALYZE
  - PLAN
  - EXECUTE
  - VALIDATE
  - REPORT
- 日志记录必须对齐 `.codex/DSL/rules/02-logs-rules.adoc`。

### Stage 1: 需求文档

目标：把口语化需求整理成结构化需求说明，并形成可裁决验收标准。

最低产物（字段对齐）：
- Background & Motivation
- Task Objective
- Task Scope（modules + excludes）
- Constraints & Dependencies
- Acceptance Criteria
- Risks & Notes

参考模板：`requirements/requirements-spec.md`

### Stage 2: 需求原型

目标：将需求说明转化为可实现原型（流程、接口、数据状态）。

最低产物（字段对齐）：
- Feature Flow
- API Draft
- Data and State Changes
- Error Cases
- Risks & Notes

参考模板：`prototype/feature-prototype.md`

### Stage 3: 任务划分

目标：按原型拆分为符合 DSL 的 Task/Sub Task。

最低产物（字段对齐）：
- Task Overview
- Task Meta（task.id/name/type/version/status）
- Task Scope（task.scope.modules / task.scope.excludes）
- Task Completion Policy（task.completion.success / failure）
- Sub Task Blocks（Overview/Meta/Completion Policy/Risks & Notes/Execute Report）

强制约束：
- 任务清单首项必须为 PLAN 入口任务。
- 后续 FUNC/TEST/DOCS 子任务必须显式声明 depends_on。

参考模板：`planning/task-breakdown.md`

### Stage 4: 系统开发

目标：按任务清单实现系统改动。

执行要求（流程对齐）：
- ANALYZE: 理解任务与 DSL 约束
- PLAN: 输出路径级文件变更计划
- EXECUTE: 仅修改已声明文件
- VALIDATE: 明确通过/不通过与违规项
- REPORT: 输出执行结果与风险

最低产物：
- 执行计划文件清单
- 实际修改文件清单
- Execute Report（执行摘要、关键变更、验证与证据、问题与结论）

参考模板：`delivery/dev-implementation-report.md`

### Stage 5: 系统测试

目标：验证实现满足验收标准并给出可交付结论。

执行要求：
- 至少覆盖关键路径与主要异常路径。
- 失败用例需要根因说明和修复记录。

最低产物（字段对齐）：
- Test Scope
- Test Cases and Results
- Failure Root Cause and Fixes
- Residual Risks
- Delivery Conclusion

参考模板：`testing/system-test-report.md`

## Output Contract (Per Task)

每次执行 `dev-core`，最终输出必须包含：
- 需求文档摘要（含 Scope/Acceptance/Risks）
- 需求原型摘要（含 API/流程/状态变化）
- 任务划分结果（含 Task Meta、Sub Task 依赖）
- 开发实现说明（含 PLAN/EXECUTE 对应文件清单）
- 系统测试结果（含失败根因与残余风险）

## Boundary

- `dev-core` 截止到“系统测试完成并可交付”。
- 发布流程（发布检查、发布说明、回滚演练）由 `release` skill 负责。
