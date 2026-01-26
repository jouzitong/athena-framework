## Basic Rules Explanation (For Codex)

⚠️⚠️⚠️ Rules Explanation (Must read before executing any task)

- Codex must follow all rule files under `codex/rules/` as the single source of truth.
- Codex can only execute tasks in `codex/tasks/` with status `READY`.
- After execution, Codex must update the task status to `DONE`.
- Codex must not change the DSL structure; only modify the specified blocks (except tasks under `updates/`).
- Execution reports must be written to `codex/logs/` and map one-to-one with tasks.
- Task execution must follow `codex/flows/task-execution-flow.adoc`. 
- Task generation must follow the relevant flow in `codex/flows/gen/` (and the rules in `codex/rules/01-task-gen-rules.adoc`).
- External framework note: `org.athena` refers to the local directory `./../../athena-framework`. Because this directory is large, only read it when a task touches or references `org.athena` classes.
- Reference docs can be found in `README.adoc` (for developers only; do not treat it as rules).
- If the task below is already marked completed, ignore it.

## 基本规则说明 (For 开发者)

.⚠️⚠️⚠️ 规则说明（在执行任务前必须阅读以下规则）

- Codex 必须遵守 `codex/rules/` 目录下的所有规则文件，作为唯一权威。
- Codex 只能执行 `codex/tasks/` 目录下状态为 `READY` 的任务。
- Codex 执行完成后，必须将任务状态改为 `DONE`。
- Codex 不得修改 DSL 结构，只能修改指定 Block（除执行 `updates/` 任务外）。
- 执行报告必须存放在 `codex/logs/` 目录下，并与任务一一对应。
- 任务执行必须遵循 `codex/flows/task-execution-flow.adoc`。
- 任务生成必须遵循 `codex/flows/gen/` 中对应的流程，并受 `codex/rules/01-task-gen-rules.adoc` 约束。
- 外部框架说明：`org.athena` 指向本地目录 `./../../athena-framework`。该目录体量较大，仅在任务涉及或引用 `org.athena` 类时再读取其代码。
- `README.adoc` 仅供开发者参考，不作为规则约束。
- 如果下面的任务标记已完成（completed），请忽略。

## 任务详情
