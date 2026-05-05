# 图书收藏 API 设计

日期：2026-05-05
项目：superpowers-springboot-lab

## 目标

构建一个小型 Spring Boot 3 API，用于管理个人图书收藏。这个练习的主要目的是实践 Superpowers 开发流程：头脑风暴、书面设计、计划制定，以及测试驱动实现。

第一阶段要刻意保持小而完整。它需要覆盖真实 Spring Boot API 开发中的持久化、校验、错误处理、JUnit 5 和 MockMvc，但暂不引入认证、分页或复杂业务规则。

## 范围

项目会作为一个新的独立 Maven 项目创建在：

```text
/Users/yukang/Documents/project/person/superpowers-springboot-lab
```

它不会加入现有的 `java-middleware-lab` 多模块仓库。

技术栈：

- Spring Boot 3
- JDK 17
- Maven
- H2
- JUnit 5
- MockMvc

## API

API 使用 JSON 和标准 REST 风格端点：

```text
POST   /api/books           创建一本收藏图书
GET    /api/books           查询收藏图书列表
PATCH  /api/books/{id}/read 将一本图书标记为已读
DELETE /api/books/{id}      删除一本收藏图书
```

预期状态码：

```text
POST   /api/books           201 Created
GET    /api/books           200 OK
PATCH  /api/books/{id}/read 200 OK
DELETE /api/books/{id}      204 No Content
```

## 数据模型

一本图书包含以下字段：

```text
id        系统生成
title     必填的图书标题，不能为空白
author    可选的作者姓名
read      是否已读，创建时默认为 false
createdAt 系统在创建时生成
```

删除采用物理删除。

## 架构

使用简单的分层 Spring Boot 架构：

```text
Controller -> Service -> Repository -> H2 Database
```

职责划分：

- `BookController` 负责 HTTP 路由、JSON 输入输出和响应状态码。
- `BookService` 实现应用用例。
- `BookRepository` 使用 Spring Data JPA 将图书持久化到 H2。
- `Book` 是 JPA 实体。
- DTO 定义 API 契约，避免直接暴露实体。

这样既能保持设计简单，也能保留清晰边界，方便测试和后续扩展。

## 组件

核心组件：

```text
SuperpowersSpringbootLabApplication
Book
BookRepository
BookService
BookController
CreateBookRequest
BookResponse
ErrorResponse
BookNotFoundException
GlobalExceptionHandler
```

`CreateBookRequest` 包含 `title` 和 `author`。它会校验 `title` 不能为空白。

`BookResponse` 包含 API 响应字段：`id`、`title`、`author`、`read` 和 `createdAt`。

`BookService` 提供四个用例：

- 创建图书
- 查询所有图书
- 将图书标记为已读
- 删除图书

`GlobalExceptionHandler` 将校验失败和资源不存在错误映射为简单的 JSON 错误响应。

## 数据流

创建图书：

1. 客户端发送包含 `title` 和可选 `author` 的 JSON。
2. Controller 校验请求体。
3. Service 创建图书，设置 `read=false`，并生成 `createdAt`。
4. Repository 将图书保存到 H2。
5. Controller 返回 `201 Created` 和 `BookResponse`。

查询图书列表：

1. Controller 接收 `GET /api/books`。
2. Service 按 `createdAt ASC, id ASC` 加载所有图书。
3. Controller 返回 `BookResponse` JSON 数组。

将图书标记为已读：

1. Controller 接收 `PATCH /api/books/{id}/read`。
2. Service 根据 id 加载图书。
3. 如果图书不存在，Service 抛出 `BookNotFoundException`。
4. 如果图书存在，Service 设置 `read=true`，保存后返回更新后的图书。
5. 对已经是已读状态的图书重复执行该操作，也应成功，并保持幂等。

删除图书：

1. Controller 接收 `DELETE /api/books/{id}`。
2. Service 检查图书是否存在。
3. 如果图书不存在，Service 抛出 `BookNotFoundException`。
4. 如果图书存在，Service 执行物理删除。
5. Controller 返回 `204 No Content`。

## 错误处理

第一阶段使用最小化 JSON 错误响应。

当 `title` 缺失、为 null、为空字符串或空白字符串时：

```http
400 Bad Request
```

```json
{
  "message": "Title must not be blank"
}
```

当标记已读或删除时指定的图书 id 不存在：

```http
404 Not Found
```

```json
{
  "message": "Book not found"
}
```

第一阶段不包含自定义错误码、trace id、字段级错误数组、认证错误或复杂统一响应包装。

## 测试策略

实现必须使用 TDD：

1. 先写一个失败的测试。
2. 运行测试，并确认它以预期方式失败。
3. 编写能让测试通过的最小实现。
4. 只有在测试通过后再重构。

MockMvc 集成测试是主要测试方式。测试应通过 Spring Boot 测试上下文和 H2 覆盖 HTTP API、校验、持久化和响应 JSON。

初始测试清单：

```text
POST /api/books
- 合法 title 和 author 返回 201，并包含创建后的图书、read=false，以及存在的 createdAt。
- 缺失或空白 title 返回 400，并包含 message "Title must not be blank"。

GET /api/books
- 没有图书时返回空数组。
- 多本图书按稳定的 createdAt/id 顺序返回。

PATCH /api/books/{id}/read
- 已存在图书返回 200，并且 read=true。
- 不存在的图书返回 404，并包含 message "Book not found"。

DELETE /api/books/{id}
- 已存在图书返回 204，并且该图书不再出现在列表中。
- 不存在的图书返回 404，并包含 message "Book not found"。
```

只有当某个行为通过 MockMvc 表达起来不够清晰时，才考虑额外添加 Service 单元测试。第一阶段不强制要求 Service 单元测试。

## 不在范围内

第一阶段不包含：

- 登录或用户
- 分页
- 搜索或筛选
- 更新标题或作者
- 软删除
- ISBN 或分类
- 复杂统一响应体
- 生产数据库配置
- Docker 打包

## 审批关卡

该设计经过审阅并确认后，下一步使用 `writing-plans` skill 创建实现计划。在实现计划写好并获得确认之前，不开始编码实现。
