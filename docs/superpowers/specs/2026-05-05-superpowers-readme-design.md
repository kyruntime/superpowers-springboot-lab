# Superpowers 学习 README 设计

日期：2026-05-05
项目：superpowers-springboot-lab

## 目标

为项目新增 `README.md`，用中文面向新手解释今天学习和实践的 Superpowers 使用流程。

这份 README 既要说明本项目是一个 Spring Boot 图书收藏 API，也要把它作为例子，讲清楚如何用 Superpowers 从需求走到设计、计划、实现、审查、验证、合并和发布。

## 读者

目标读者是刚接触 Superpowers 和 AI 编程工作流的新手。

读者可能已经会一点 Git、Java 或 Spring Boot，但不一定理解：

- 为什么不能一上来就让 AI 写代码
- spec、plan、worktree、review、verification 分别有什么用
- 哪些 skill 是用户应该主动点名的
- 哪些 skill 更像流程内部的辅助组件

## 内容范围

README 需要包含：

- 项目简介
- Superpowers 是什么
- 完整开发流程
- Mermaid 流程图
- 常见 skill 的作用说明
- 用户常用 skill 与内部辅助 skill 的区分
- 本项目 Spring Boot 图书 API 的实践案例
- 给新手的使用建议

README 不需要包含：

- 详细 Java 教程
- Spring Boot 每个注解的完整解释
- Superpowers 插件安装教程
- 所有 skill 的穷尽列表
- 过于官方化或营销化的介绍

## 写作风格

使用中文，语气轻松、清楚、像学习笔记。

要求：

- 不写得太官方
- 不使用大段抽象术语
- 先解释“为什么”，再解释“怎么做”
- 用本项目真实经历串起来
- 保留 Markdown 格式，适合放到 GitHub
- Mermaid 图应能在 GitHub README 中渲染

## README 结构

建议结构：

```text
# superpowers-springboot-lab

## 这个项目是什么
## Superpowers 是什么
## 我们用 Superpowers 跑过的一整套流程
## 流程图
## 常见 skill 速查
## 哪些 skill 是用户常用的
## 哪些 skill 更像内部辅助组件
## 用本项目举个完整例子
## 新手使用建议
## 项目当前 API
## 本地运行与测试
```

## Mermaid 流程图

README 中需要包含一个主流程图，表达：

```text
需求 -> brainstorming -> spec -> writing-plans -> worktree -> 执行实现 -> code review -> verification -> finishing -> GitHub
```

也可以包含一个简短分支说明：

```text
实现方式可以选择 executing-plans 或 subagent-driven-development
```

## Skill 分类

用户常用 skill：

- `brainstorming`
- `writing-plans`
- `executing-plans`
- `subagent-driven-development`
- `requesting-code-review`
- `verification-before-completion`
- `finishing-a-development-branch`

内部辅助或偏流程组件：

- `using-git-worktrees`
- 实现子代理
- 规格审查子代理
- 代码质量审查子代理
- 最终代码审查子代理

分类说明要强调：用户不一定每天直接点名内部辅助组件，但理解它们存在有助于知道流程为什么安全。

## 本项目案例

README 需要结合本项目讲两个实际例子：

1. 初始图书收藏 API：
   - 写中文 spec
   - 写 implementation plan
   - 建 worktree
   - 实现 Spring Boot API
   - MockMvc 测试
   - code review
   - verification
   - 合并 main
   - 推到 GitHub

2. 新增 `GET /api/books/{id}`：
   - 先澄清成功响应是否复用 `BookResponse`
   - 写 spec 和 plan
   - 用子代理驱动实现
   - 两阶段审查
   - 全量测试
   - 合并回 main

## API 简介

README 需要列出现有 API：

```text
POST   /api/books
GET    /api/books
GET    /api/books/{id}
PATCH  /api/books/{id}/read
DELETE /api/books/{id}
```

只做简要说明，不展开成完整接口文档。

## 验证

README 需要包含本地测试命令：

```bash
mvn test
```

说明该命令会运行 MockMvc 集成测试，验证 API 行为。

## 审批关卡

该 README 设计确认后，下一步使用 `writing-plans` skill 创建实现计划。实现计划确认后再写入 `README.md`。
