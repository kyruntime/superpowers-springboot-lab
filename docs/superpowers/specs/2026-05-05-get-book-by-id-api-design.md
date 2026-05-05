# 根据 ID 查询单本图书 API 设计

日期：2026-05-05
项目：superpowers-springboot-lab

## 目标

在现有图书收藏 API 上新增一个端点，用于根据 id 查询单本图书。

新增端点：

```text
GET /api/books/{id}
```

该功能只补齐单本查询能力，不改变已有创建、列表、标记已读和删除接口行为。

## 范围

本次只包含：

- id 存在时返回 `200 OK` 和图书详情
- id 不存在时返回 `404 Not Found` 和简单 JSON 错误响应
- 成功响应复用现有 `BookResponse`
- 错误响应复用现有 `ErrorResponse`
- 不引入登录、分页、复杂统一响应体或额外查询条件

## API 契约

请求：

```http
GET /api/books/{id}
```

成功响应：

```http
200 OK
```

```json
{
  "id": 1,
  "title": "Clean Code",
  "author": "Robert C. Martin",
  "read": false,
  "createdAt": "2026-05-05T08:00:00Z"
}
```

不存在响应：

```http
404 Not Found
```

```json
{
  "message": "Book not found"
}
```

## 架构

继续沿用现有分层结构：

```text
BookController -> BookService -> BookRepository -> H2 Database
```

新增职责：

- `BookController` 增加 `GET /api/books/{id}` 路由。
- `BookService` 增加 `getBook(Long id)` 用例。
- `BookRepository` 继续使用已有 `findById`，不需要新增方法。
- `BookNotFoundException` 和 `GlobalExceptionHandler` 继续处理 id 不存在场景。

该设计保持改动最小，并复用现有错误处理和 DTO 契约。

## 数据流

根据 id 查询图书：

1. Controller 接收 `GET /api/books/{id}`。
2. Controller 将路径参数 `id` 传给 `BookService#getBook`。
3. Service 调用 `bookRepository.findById(id)`。
4. 如果图书存在，Service 将 `Book` 映射为 `BookResponse`。
5. Controller 返回 `200 OK` 和 `BookResponse`。
6. 如果图书不存在，Service 抛出 `BookNotFoundException`。
7. `GlobalExceptionHandler` 将异常映射为 `404 Not Found` 和 `{"message":"Book not found"}`。

## 测试与验证

本次实现先不强调 TDD 红绿循环，但需要通过 MockMvc 集成测试验证行为。

新增测试：

```text
GET /api/books/{id}
- 已存在图书返回 200，并包含 id、title、author、read、createdAt。
- 不存在图书返回 404，并包含 message "Book not found"。
```

完成后运行：

```bash
mvn test
```

要求全量测试通过，并确认已有接口行为没有回归。

## 不在范围内

本次不包含：

- 登录或用户
- 分页
- 搜索或筛选
- 更新标题或作者
- 软删除
- 复杂统一响应体
- 自定义错误码
- 生产数据库配置

## 审批关卡

该设计确认后，下一步使用 `writing-plans` skill 创建实现计划。实现计划确认后再开始修改代码。
