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
5. 选择执行方式：`executing-plans` 或 `subagent-driven-development`
6. 实现功能
7. 用 reviewer 子代理做规格审查和代码质量审查
8. 用 `verification-before-completion` 重新跑测试
9. 用 `finishing-a-development-branch` 合并或保留分支
10. 最后推送到 GitHub

## 流程图

```mermaid
flowchart TD
    A["提出需求"] --> B["brainstorming<br/>澄清需求，形成设计"]
    B --> C["写 spec<br/>把设计落成文档"]
    C --> D["writing-plans<br/>写实现计划"]
    D --> E["using-git-worktrees<br/>创建隔离工作区"]
    E --> F{"选择执行方式"}
    F --> G["executing-plans<br/>本会话按计划执行"]
    F --> H["subagent-driven-development<br/>子代理分任务执行"]
    G --> I["实现功能"]
    H --> I
    I --> J["requesting-code-review<br/>代码审查"]
    J --> K["verification-before-completion<br/>重新运行验证命令"]
    K --> L["finishing-a-development-branch<br/>合并、PR、保留或丢弃"]
    L --> M["GitHub<br/>推送代码"]
```

## 常见 skill 速查

下面这些 skill 可以理解成 Superpowers 流程里的“工具卡片”。你不用一开始全背下来，先知道它们大概负责什么就行。

| Skill | 作用 | 什么时候用 |
| --- | --- | --- |
| `brainstorming` | 把模糊想法变成清楚设计 | 新功能、改行为、写重要文档前 |
| `writing-plans` | 把 spec 拆成可执行计划 | 设计确认后、动手实现前 |
| `executing-plans` | 在当前会话按计划执行 | 想让 AI 一步步直接做 |
| `subagent-driven-development` | 派子代理分任务实现和审查 | 任务稍复杂、希望更稳时 |
| `requesting-code-review` | 请求代码审查 | 功能完成后、合并前 |
| `verification-before-completion` | 完成前重新验证 | 说“完成”“通过”“可合并”之前 |
| `finishing-a-development-branch` | 收尾分支 | 测试通过后，选择合并、PR、保留或丢弃 |
| `using-git-worktrees` | 创建隔离工作区 | 执行计划前，避免污染主分支 |

## 哪些 skill 是用户常用的

你作为使用者，最常主动点名的是这些：

- `brainstorming`
- `writing-plans`
- `executing-plans`
- `subagent-driven-development`
- `requesting-code-review`
- `verification-before-completion`
- `finishing-a-development-branch`

一个比较顺手的口诀是：

```text
先聊清楚 -> 写设计 -> 写计划 -> 执行 -> 审查 -> 验证 -> 收尾
```

对应到 skill：

```text
brainstorming -> writing-plans -> executing-plans/subagent-driven-development -> requesting-code-review -> verification-before-completion -> finishing-a-development-branch
```

## 哪些 skill 更像内部辅助组件

有些 skill 或子代理，你不一定每天直接点名，但它们在流程里很重要：

- `using-git-worktrees`：帮你创建隔离工作区，避免在 `main` 上直接乱改。
- 实现子代理：负责某个具体任务，比如只改 Controller 和 Service。
- 规格审查子代理：检查“有没有按 spec 做”，防止做少或做多。
- 代码质量审查子代理：检查代码风格、边界、风险和可维护性。
- 最终代码审查子代理：合并前再整体看一遍。

这些东西有点像厨房里的备菜、试味和出餐检查。用户看到的是一道菜，流程里其实有很多小关卡在防止翻车。

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
