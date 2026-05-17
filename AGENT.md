# Athena Framework Skill Index

这个文件用于声明项目级 Codex Skills 的入口、适用场景和调用约定。

## Skill Root

- `.codex/skills/`

## Skills

### 1) dev-core
- Path: `.codex/skills/dev-core/SKILL.md`
- Use when: 从需求到开发测试的完整交付流程
- Keywords: `dev-core`, `开发`, `需求`, `任务划分`, `系统测试`
- Mandatory Stages: `需求文档 -> 需求原型 -> 任务划分 -> 系统开发 -> 系统测试`
- DSL Alignment: 必须对齐 `.codex/DSL/rules/*` 与 `.codex/DSL/flows/*`

### 2) code-review
- Path: `.codex/skills/code-review/SKILL.md`
- Use when: 代码审查、风险扫描、回归影响分析
- Keywords: `code-review`, `review`, `风险`, `回归`

### 3) test-fix
- Path: `.codex/skills/test-fix/SKILL.md`
- Use when: 测试失败定位与修复
- Keywords: `test-fix`, `修测试`, `单测失败`, `集成测试失败`

### 4) release
- Path: `.codex/skills/release/SKILL.md`
- Use when: 发布前检查、发布说明、回滚准备
- Keywords: `release`, `发版`, `上线检查`, `回滚`

## Invocation Examples

- `使用 dev-core skill：根据以下口语化需求，先产出 DSL 对齐的需求文档，再输出原型、任务划分、开发与测试结果。`
- `使用 dev-core skill：严格按五阶段输出，并在开发阶段按 ANALYZE/PLAN/EXECUTE/VALIDATE/REPORT 记录。`
- `开发完成后，使用 code-review 做收尾审查；上线前使用 release。`

## Skill Composition Order (Recommended)

1. `dev-core` -> 2. `test-fix` -> 3. `code-review` -> 4. `release`

## Rules

- 关键规则优先写在每个 skill 的 `SKILL.md` 中。
- `checklists/`、`templates/`、`scripts/`、`examples/` 作为复用资产。
- 如 skill 间规则冲突，以用户当前任务目标和显式指定 skill 为准。

## DSL 与 Skills 边界定义（治理基线）

### 核心定位

- DSL 是制度层：定义“做什么、何时可执行、如何判定完成/失败”。
- Skills 是执行层：定义“在 DSL 约束下，如何高质量完成某类任务”。

### DSL 职责边界（必须放在 DSL）

- 任务与子任务结构定义（必填字段、块结构、依赖关系）。
- 状态机与流转规则（TODO/READY/DOING/DONE/BLOCKED）。
- 流程与门禁（开发、测试、发布各阶段进入/退出条件）。
- 裁决标准（成功/失败判定、阻断条件、审计要求）。
- 跨 skill 的统一约束（所有 skill 都必须遵守）。

### Skills 职责边界（必须放在 Skill）

- 具体执行方法（步骤、策略、检查顺序）。
- 复用资产（脚本、模板、checklist、示例）。
- 场景化质量要求（如测试修复、代码审查、发布检查的实践细则）。
- 效率优化与工程经验沉淀（不改变 DSL 契约）。

### 禁止越界规则

- Skill 不得新增或修改 DSL 状态机、门禁和裁决标准。
- Skill 不得绕过 DSL 的 READY 执行约束与流程步骤。
- DSL 不承载具体脚本实现细节或场景化操作手册。

### 冲突处理优先级

1. DSL（`.codex/DSL/rules/*` + `.codex/DSL/flows/*`）优先于 Skill。
2. 若确需调整规则，先走 DSL 升级任务（`.codex/DSL/updates/*`），再同步 Skill。
3. Skill 之间冲突时，以用户当前任务目标和显式指定 skill 为准。

### 后续优化归属准则（用于开发期判定）

- 改“制度”：放 DSL（规则、流程、状态、门禁、裁决）。
- 改“做法”：放 Skill（步骤、模板、脚本、检查清单）。
- 同时涉及两者时：
  - 先更新 DSL 约束；
  - 再更新 Skill 的执行方式与资产；
  - 最后补充升级记录，确保可追踪。
