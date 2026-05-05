# superpowers-springboot-lab

这是一个用 Spring Boot 3 写的小型图书收藏 API。

不过它不只是一个普通 CRUD 练习。这个项目更像是一份“AI 编程流程练习册”：我们用它完整跑了一遍 Superpowers 工作流，从一个想法开始，经过需求澄清、写 spec、写 plan、创建 worktree、实现代码、代码审查、验证测试、合并分支，最后推到 GitHub。

如果你刚开始用 AI 写代码，这个项目最值得看的不是某一行 Java，而是这套流程：它能帮你少一点“AI 直接开写然后写偏了”的痛苦。

## 这个项目是什么

项目本身是一个个人图书收藏 API，当前支持：

```text
POST   /api/books           创建一本收藏图书
GET    /api/books           查询收藏图书列表
GET    /api/books/{id}      根据 id 查询单本图书
PATCH  /api/books/{id}/read 将一本图书标记为已读
DELETE /api/books/{id}      删除一本收藏图书
```

技术栈很朴素：

- Spring Boot 3
- JDK 17
- Maven
- Spring Web
- Spring Data JPA
- H2
- JUnit 5
- MockMvc

代码结构也刻意保持简单：

```text
BookController -> BookService -> BookRepository -> H2 Database
```

## Superpowers 是什么

你可以把 Superpowers 理解成一套“让 AI 编程更有章法”的工作流。

普通用法经常是这样：

```text
我：帮我加个接口
AI：好的，开始改代码
```

这很快，但也容易出问题。需求还没说清楚，AI 就已经开始写；写完以后你才发现接口响应格式不对、测试没覆盖、分支没隔离、代码也没 review。

Superpowers 想解决的就是这个问题。它把开发拆成一串比较稳的步骤：

```text
先弄清楚要做什么
再写设计文档
再写实现计划
再隔离分支开发
再验证和审查
最后合并或发布
```

它不是让流程变复杂，而是把容易出错的地方提前拦住。

## 我们用 Superpowers 跑过的一整套流程

这次项目里，我们大概按这个节奏走：

1. 先用 `brainstorming` 把想法说清楚
2. 写中文 spec，确认到底要做什么
3. 用 `writing-plans` 写 implementation plan
4. 用 `using-git-worktrees` 创建隔离 worktree
5. 用 `executing-plans` 按计划执行
6. 实现功能
7. 用 reviewer 子代理做规格审查和代码质量审查
8. 用 `verification-before-completion` 重新跑测试
9. 用 `finishing-a-development-branch` 合并或保留分支
10. 最后推送到 GitHub

如果任务比较复杂，也可以把第 5 步换成 `subagent-driven-development`，让子代理拆任务并行处理。它属于特定场景增强，不是你每天都要手动触发的主流程节点。

## 流程图

```mermaid
flowchart TD
    A["提出需求"] --> B["brainstorming<br/>澄清需求，形成设计"]
    B --> C["写 spec<br/>把设计落成文档"]
    C --> D["writing-plans<br/>写实现计划"]
    D --> E["using-git-worktrees<br/>创建隔离工作区"]
    E --> F["executing-plans<br/>本会话按计划执行"]
    F -. "复杂任务可选" .-> G["subagent-driven-development<br/>子代理分任务执行"]
    F --> I["实现功能"]
    G --> I
    I --> J["requesting-code-review<br/>代码审查"]
    J --> K["verification-before-completion<br/>重新运行验证命令"]
    K --> L["finishing-a-development-branch<br/>合并、PR、保留或丢弃"]
    L --> M["GitHub<br/>推送代码"]
```

## 常见 skill 速查

Superpowers 不是所有 skill 都需要你手动点名。

更准确地说，skill 可以分成三类：用户经常直接触发的“主流程节点”、Agent 在流程中自动或半自动调用的“辅助机制”，以及只在特定场景才会用到的“插件式增强”。你不用一开始背完整列表，先知道它们分别处在开发流程的哪个位置就行。

| Skill | 作用 | 什么时候用 | 实际会做什么 |
| --- | --- | --- | --- |
| `brainstorming` | 把模糊想法变成清楚设计 | 新功能、改行为、写重要文档前 | 通过提问澄清目标、边界、取舍和验收标准，先把“想做什么”说清楚。 |
| `writing-plans` | 把 spec 拆成可执行计划 | 设计确认后、动手实现前 | 把需求拆成文件级步骤、测试点、验证命令和风险提示，形成可照着执行的 plan。 |
| `executing-plans` | 在当前会话按计划执行 | 想让 AI 一步步直接做 | 按 plan 逐项实现、阶段性检查结果，遇到偏差时停下来修正执行路线。 |
| `subagent-driven-development` | 派子代理分任务实现和审查 | 任务稍复杂、希望更稳时 | 把独立任务分给子代理并行处理，再汇总代码、审查结果和遗留问题。 |
| `requesting-code-review` | 请求代码审查 | 功能完成后、合并前 | 让另一个审查视角检查 bug、回归风险、遗漏测试和需求不匹配的地方。 |
| `verification-before-completion` | 完成前重新验证 | 说“完成”“通过”“可合并”之前 | 先运行能证明结果的命令，读完整输出，再根据证据判断是否真的完成。 |
| `finishing-a-development-branch` | 收尾分支 | 测试通过后，选择合并、PR、保留或丢弃 | 引导你选择下一步：合并、开 PR、保留分支，或丢弃实验性改动。 |
| `using-git-worktrees` | 创建隔离工作区 | 执行计划前，避免污染主分支 | 创建独立 worktree，在隔离目录里开发，减少和主分支或其他任务互相干扰。 |

## 1. 用户常用的主流程 skill

这些是你作为用户最常直接用的 skill。它们对应真实开发阶段：

```text
brainstorming
writing-plans
executing-plans
requesting-code-review
verification-before-completion
finishing-a-development-branch
systematic-debugging
```

一个比较顺手的口诀是：

```text
先聊清楚 -> 写设计 -> 写计划 -> 执行 -> 审查 -> 验证 -> 收尾
```

对应到 skill：

```text
brainstorming
-> writing-plans
-> executing-plans
-> requesting-code-review
-> verification-before-completion
-> finishing-a-development-branch
```

如果是在修 bug，中间通常会插入：

```text
systematic-debugging
```

它负责先定位问题、复现现象、验证假设，再进入修复，而不是看到报错就马上猜一个改法。

## 2. 半自动或内部辅助 skill

这些 skill 通常不是你主动点名，而是在主流程里被 Agent 用来保证执行更稳：

- `using-git-worktrees`
- `test-driven-development`
- `receiving-code-review`

### `using-git-worktrees`

这是执行前的隔离策略。

你一般不会一上来就说“使用 `using-git-worktrees`”。更常见的情况是：写完 plan 之后，准备执行之前，Agent 判断这次会改代码，于是自动创建一个隔离 worktree。

它解决的是一个很实际的问题：不要把实验性修改直接混在当前分支里。

### `test-driven-development`

如果 plan 里明确写了 TDD，执行阶段就会按这个方式走：先写失败测试，再实现功能，再让测试通过。

你也可以手动要求：

```text
这一步使用 test-driven-development。
```

但更多时候，它是执行计划的一部分，而不是一个单独的入口。

### `receiving-code-review`

这个通常配合 `requesting-code-review` 使用。

一个负责发起审查，另一个负责接收、理解和处理审查意见。重点不是“审查说什么都照做”，而是判断反馈是否准确、是否需要改、改完后是否还要重新验证。

## 3. 特定场景 skill

这些不是主流程必须项，有对应场景才会用：

| Skill | 适合场景 |
| --- | --- |
| `frontend-design` | 做前端页面、组件、视觉优化或交互设计。 |
| `subagent-driven-development` | 任务可以拆成多个独立部分，希望子代理并行执行和审查。 |
| `dispatching-parallel-agents` | 有多个互不依赖的问题，可以并行调查或处理。 |
| `writing-skills` | 你想自己写新的 skill，或改已有 skill。 |
| `jira-worktime-summary` | 从 Jira 汇总工时、成本、人力投入和阶段统计。 |

所以看到一个 skill 时，先不要问“我要不要手动点它”，而是先问：

```text
它是一个开发阶段，还是一个辅助机制？
```

例如：

```text
writing-plans = 开发阶段
using-git-worktrees = 辅助机制
```

这个判断很重要。它能帮你把 Superpowers 当成一套流程来用，而不是把它误解成一堆需要全部记住的命令。

## 用本项目举个完整例子

### 例子一：从零做图书收藏 API

一开始我们不是直接写 Java，而是先写了中文 spec：

- 要做一个图书收藏 API
- 使用 Spring Boot 3
- 使用 H2 做本地持久化
- 用 MockMvc 做集成测试
- 第一阶段不做登录、分页、搜索、Docker

然后写 implementation plan，把工作拆成：

- 初始化 Maven 项目
- 实现创建图书
- 实现列表查询
- 实现标记已读
- 实现删除图书
- 补充错误处理
- 跑全量测试

实现时我们用了 worktree，避免直接在 `main` 上改。完成后做了代码审查、测试验证，再合并回 `main`，最后推送到 GitHub。

### 例子二：新增 `GET /api/books/{id}`

后来我们又加了一个接口：

```text
GET /api/books/{id}
```

这次需求看起来很小，但 Superpowers 仍然要求先澄清。

我们先确认了一个关键点：成功响应复用现有 `BookResponse`。

然后写 spec，明确：

- id 存在时返回 `200 OK`
- id 不存在时返回 `404 Not Found`
- 错误响应继续使用：

```json
{
  "message": "Book not found"
}
```

接着写 plan，再用 `subagent-driven-development` 执行：

- Task 1：改 `BookService` 和 `BookController`
- Task 2：补 MockMvc 测试
- 每个 Task 后都做规格审查和代码质量审查
- 最后跑 `mvn test`
- 合并回 `main`

这个例子说明：即使只是一个小接口，也可以用很轻量的 spec 和 plan 把事情做稳。

## 新手使用建议

如果你刚开始用 Superpowers，我建议先记住这些：

1. 不要一上来就让 AI 写代码  
   先让它用 `brainstorming` 问清楚需求。

2. 重要需求一定写 spec  
   spec 不是官样文章，它是防止 AI 写偏的锚点。

3. 写代码前先写 plan  
   plan 能把“大任务”拆成小任务，也方便审查。

4. 不要迷信“看起来没问题”  
   用 `verification-before-completion` 重新跑命令，用结果说话。

5. 合并前要 code review  
   就算是 AI 写的，也要让另一个视角检查一遍。

6. 学会问“下一步应该用哪个 skill”  
   这很有用，能帮你把流程接上，不会做到一半乱掉。

## 项目当前 API

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| `POST` | `/api/books` | 创建一本收藏图书 |
| `GET` | `/api/books` | 查询所有收藏图书 |
| `GET` | `/api/books/{id}` | 根据 id 查询单本图书 |
| `PATCH` | `/api/books/{id}/read` | 将图书标记为已读 |
| `DELETE` | `/api/books/{id}` | 删除一本收藏图书 |

## 本地运行与测试

运行测试：

```bash
mvn test
```

这个命令会运行 MockMvc 集成测试，验证 API 的主要行为，包括：

- 创建图书
- 标题校验
- 查询列表
- 根据 id 查询单本图书
- 标记已读
- 删除图书
- 404 错误响应

如果测试通过，说明当前 API 的核心行为是稳定的。

## 最后一句

Superpowers 的重点不是“多几个步骤”，而是让 AI 编程从随手一改，变成有设计、有计划、有验证、有收尾。

对于小项目，它能帮你养成好习惯；对于大项目，它能救命。
